package kz.greetgo.conf.core;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

//TODO pompei проверить на используемость, и, если не используется, - удалить
public class ConfCallbackCached implements ConfCallback {

  private final ConfCallback                                source;
  private final long                                        timeoutMs;
  private final ConcurrentHashMap<String, Optional<String>> params = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Integer>          sizes  = new ConcurrentHashMap<>();
  private final LongSupplier                                currentTimeMillis;

  private final AtomicLong lastCheck = new AtomicLong(0);

  public ConfCallbackCached(ConfCallback source, long timeoutMs, LongSupplier currentTimeMillis) {
    this.source            = source;
    this.timeoutMs         = timeoutMs;
    this.currentTimeMillis = currentTimeMillis;
  }

  @Override
  public String readParam(String paramPath) {
    check();
    {
      Optional<String> valueOpt = params.get(paramPath);
      //noinspection OptionalAssignedToNull
      if (valueOpt != null) {
        return valueOpt.orElse(null);
      }
    }
    {
      String value = source.readParam(paramPath);
      params.put(paramPath, Optional.ofNullable(value));
      return value;
    }
  }

  @Override
  public int readParamSize(String paramPath) {
    check();
    {
      Integer size = sizes.get(paramPath);
      if (size != null) {
        return size;
      }
    }
    {
      int size = source.readParamSize(paramPath);
      sizes.put(paramPath, size);
      return size;
    }
  }

  private void check() {
    long now         = currentTimeMillis.getAsLong();
    long lastCheckMs = lastCheck.get();

    if (lastCheckMs == 0) {
      lastCheck.set(now);
      return;
    }

    if (lastCheckMs + timeoutMs < now) {
      params.clear();
      sizes.clear();
      lastCheck.set(now);
    }
  }

  @Override
  public String readEnv(String envName) {
    throw new RuntimeException("Not impl yet: ConfCallbackCached.readEnv");
  }
}
