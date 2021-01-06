package kz.greetgo.conf.core;

import kz.greetgo.conf.hot.ForcibleInit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.LongSupplier;

import static kz.greetgo.conf.ConfUtil.findAnnotation;

public class ConfImplBuilder<T> {

  private final Class<T>   confClass;
  private final ConfAccess confAccess;

  private long changeCheckTimeoutMs = 3000;

  private ConfImplBuilder(Class<T> confClass, ConfAccess confAccess) {
    this.confClass  = confClass;
    this.confAccess = confAccess;
  }

  public static <T> ConfImplBuilder<T> confImplBuilder(Class<T> confClass, ConfAccess confAccess) {
    return new ConfImplBuilder<>(confClass, confAccess);
  }

  private LongSupplier currentTimeMillis = System::currentTimeMillis;

  public ConfImplBuilder<T> changeCheckTimeoutMs(long changeCheckTimeoutMs) {
    this.changeCheckTimeoutMs = changeCheckTimeoutMs;
    return this;
  }

  private Function<String, String> envAccess = System::getenv;

  public ConfImplBuilder<T> envAccess(Function<String, String> envAccess) {
    this.envAccess = envAccess;
    return this;
  }

  public ConfImplBuilder<T> currentTimeMillis(LongSupplier currentTimeMillis) {
    Objects.requireNonNull(currentTimeMillis);
    this.currentTimeMillis = currentTimeMillis;
    return this;
  }

  public T build() {
    return new Impl(confClass, confAccess, envAccess, changeCheckTimeoutMs, currentTimeMillis)
             .confImplToCallback
             .impl();
  }

  private class Impl {
    final ConfAccess                       confAccess;
    final ConfImplToCallback<T>            confImplToCallback;
    final long                             changeCheckTimeoutMs;
    final LongSupplier                     currentTimeMillis;
    final Function<String, String>         envAccess;
    final AtomicReference<ConfContentData> data          = new AtomicReference<>();
    final AtomicLong                       lastCheckTime = new AtomicLong(0);

    final ConfCallback confCallbackImpl = new ConfCallback() {
      @Override
      public String readParam(String paramPath) {
        prepareData();
        return data.get().params.get(paramPath);
      }

      @Override
      public int readParamSize(String paramPath) {
        prepareData();
        return Optional.ofNullable(data.get().sizes.get(paramPath)).orElse(0);
      }

      @Override
      public String readEnv(String envName) {
        return envAccess.apply(envName);
      }
    };

    private Impl(Class<T> confClass, ConfAccess confAccess, Function<String, String> envAccess,
                 long changeCheckTimeoutMs, LongSupplier currentTimeMillis) {

      this.confAccess           = confAccess;
      confImplToCallback        = new ConfImplToCallback<>(confClass, confCallbackImpl);
      this.envAccess            = envAccess;
      this.changeCheckTimeoutMs = changeCheckTimeoutMs;
      this.currentTimeMillis    = currentTimeMillis;

      {
        ForcibleInit forcibleInit = findAnnotation(confClass, ForcibleInit.class);
        if (forcibleInit != null) {
          prepareData();
        }
      }
    }

    boolean freeze = false;

    private void prepareData() {

      ConfContentData currentData = data.get();

      if (currentData == null) {
        lastCheckTime.set(currentTimeMillis.getAsLong());

        synchronized (data) {
          currentData = data.get();
          if (currentData == null) {
            writeConfig();
            return;
          }
        }
      }

      if (freeze) return;

      {
        long now = currentTimeMillis.getAsLong();

        if (lastCheckTime.get() + changeCheckTimeoutMs > now) {
          return;
        }

        lastCheckTime.set(now);

        readIfModified(currentData);
      }
    }

    private void readIfModified(ConfContentData currentData) {
      Date lastModifiedAt = confAccess.lastModifiedAt();

      if (lastModifiedAt == null) {
        return;
      }

      if (currentData.lastModifiedAt >= lastModifiedAt.getTime()) {
        return;
      }

      ConfContent newContent = confAccess.load();
      if (newContent == null) {
        freeze = true;
        return;
      }

      data.set(newContent.toData(lastModifiedAt.getTime()));
    }

    private void writeConfig() {
      ConfContent confContent    = confAccess.load();
      Date        lastModifiedAt = confAccess.lastModifiedAt();
      ConfContent defaultContent = confImplToCallback.defaultContent();

      if (lastModifiedAt == null || confContent == null) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        defaultContent.insertTopComment("Created at " + sdf.format(new Date()) + '\n');
        confAccess.write(defaultContent);
        Date newLastModifiedAt       = confAccess.lastModifiedAt();
        long newLastModifiedAtMillis = newLastModifiedAt != null ? newLastModifiedAt.getTime() : currentTimeMillis.getAsLong();
        data.set(defaultContent.toData(newLastModifiedAtMillis));
        return;
      }

      ConfContent delta = defaultContent.minus(confContent);

      if (delta.isEmpty()) {
        data.set(confContent.toData(lastModifiedAt.getTime()));
        return;
      }

      {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        confContent.addComment("Edited at " + sdf.format(new Date(currentTimeMillis.getAsLong())));
        confContent.add(delta);

        confAccess.write(confContent);
        Date newLastModifiedAt = confAccess.lastModifiedAt();
        if (newLastModifiedAt == null) {
          newLastModifiedAt = new Date(currentTimeMillis.getAsLong());
        }

        data.set(confContent.toData(newLastModifiedAt.getTime()));
        return;
      }
    }

  }

}
