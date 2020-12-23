package kz.greetgo.conf.core;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConfCallbackCached implements ConfCallback {

  private final ConfCallback source;
  private final long timeoutMs;
  private final ConcurrentHashMap<String, Optional<String>> params = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Integer> sizes = new ConcurrentHashMap<>();

  private final AtomicLong lastCheck = new AtomicLong(System.currentTimeMillis());

  public ConfCallbackCached(ConfCallback source, long timeoutMs) {
    this.source = source;
    this.timeoutMs = timeoutMs;
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
    long now = System.currentTimeMillis();
    if (lastCheck.get() + timeoutMs < now) {
      params.clear();
      sizes.clear();
      lastCheck.set(now);
    }
  }
}
