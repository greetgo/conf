package kz.greetgo.conf.core;

import kz.greetgo.conf.hot.ForcibleInit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static kz.greetgo.conf.ConfUtil.findAnnotation;

public class ConfImplBuilder<T> {

  public static <T> ConfImplBuilder<T> confImplBuilder(Class<T> confClass, ConfAccess confAccess) {
    return new ConfImplBuilder<>(confClass, confAccess);
  }

  private long changeCheckTimeoutMs = 2500;

  public ConfImplBuilder<T> changeCheckTimeoutMs(long changeCheckTimeoutMs) {
    this.changeCheckTimeoutMs = changeCheckTimeoutMs;
    return this;
  }

  public T build() {
    return new Impl(confClass, confAccess, changeCheckTimeoutMs).confImplToCallback.impl();
  }

  private final Class<T>   confClass;
  private final ConfAccess confAccess;

  private ConfImplBuilder(Class<T> confClass, ConfAccess confAccess) {
    this.confClass  = confClass;
    this.confAccess = confAccess;
  }

  private class Impl {
    final ConfAccess                       confAccess;
    final ConfCallbackCached               confCallbackCached;
    final ConfImplToCallback<T>            confImplToCallback;
    final AtomicReference<ConfContentData> data = new AtomicReference<>();

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
    };

    private Impl(Class<T> confClass, ConfAccess confAccess, long changeCheckTimeoutMs) {
      this.confAccess    = confAccess;
      confCallbackCached = new ConfCallbackCached(confCallbackImpl, changeCheckTimeoutMs);
      confImplToCallback = new ConfImplToCallback<>(confClass, confCallbackCached);

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
        ConfContent confContent    = confAccess.load();
        Date        lastModifiedAt = confAccess.lastModifiedAt();
        ConfContent defaultContent = confImplToCallback.defaultContent();

        if (lastModifiedAt == null || confContent == null) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          defaultContent.insertTopComment("Created at " + sdf.format(new Date()) + '\n');
          confAccess.write(defaultContent);
          Date newLastModifiedAt = confAccess.lastModifiedAt();
          if (newLastModifiedAt == null) {
            newLastModifiedAt = new Date();
          }
          data.set(defaultContent.toData(newLastModifiedAt.getTime()));
          return;
        }

        ConfContent delta = defaultContent.minus(confContent);

        if (delta.isEmpty()) {
          data.set(confContent.toData(lastModifiedAt.getTime()));
          return;
        }

        {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          confContent.addComment("Edited at " + sdf.format(new Date()));
          confContent.add(delta);

          confAccess.write(confContent);
          Date newLastModifiedAt = confAccess.lastModifiedAt();
          if (newLastModifiedAt == null) {
            newLastModifiedAt = new Date();
          }

          data.set(confContent.toData(newLastModifiedAt.getTime()));
          return;
        }
      }

      if (freeze) return;

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
  }

}
