package kz.greetgo.conf.hot;

import kz.greetgo.conf.core.ConfAccess;
import kz.greetgo.conf.core.ConfAccessStdSerializer;
import kz.greetgo.conf.core.ConfContentSerializer;
import kz.greetgo.conf.core.ConfImplBuilder;

import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractHotConfFactory {
  private final ConcurrentHashMap<Class<?>, Object> proxyMap = new ConcurrentHashMap<>();

  public <T> T createConfig(Class<T> configInterface) {
    {
      //noinspection unchecked
      T ret = (T) proxyMap.get(configInterface);
      if (ret != null) return ret;
    }

    synchronized (proxyMap) {
      {
        //noinspection unchecked
        T ret = (T) proxyMap.get(configInterface);
        if (ret != null) return ret;
      }

      {
        T ret = buildConfigProxy(configInterface);
        proxyMap.put(configInterface, ret);
        return ret;
      }
    }
  }

  protected long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  private <T> T buildConfigProxy(Class<T> configInterface) {
    return ConfImplBuilder.confImplBuilder(configInterface, confAccess(configInterface))
                          .changeCheckTimeoutMs(autoResetTimeout())
                          .currentTimeMillis(this::currentTimeMillis)
                          .build();
  }

  /**
   * Defines auto reset timeout. It is a time interval in milliseconds to check last config modification date and time.
   * And if the date and time was changed, then it calls method `reset` for this config.
   *
   * @return auto reset timeout. Zero - auto resetting is off
   */
  protected long autoResetTimeout() {
    return 3000;
  }

  protected abstract <T> ConfAccess confAccess(Class<T> configInterface);

  public void reset() {
    proxyMap.clear();
  }

  protected ConfContentSerializer confContentSerializer() {
    return new ConfAccessStdSerializer();
  }

  public String extractInterfaceName(Class<?> configInterface) {
    ConfigFileName x = configInterface.getAnnotation(ConfigFileName.class);
    return x != null ? x.value() : configInterface.getSimpleName();
  }
}
