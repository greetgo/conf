package kz.greetgo.conf.hot;

import kz.greetgo.conf.core.ConfAccess;
import kz.greetgo.conf.core.ConfAccessStdSerializer;
import kz.greetgo.conf.core.ConfContent;
import kz.greetgo.conf.core.ConfContentSerializer;
import kz.greetgo.conf.core.ConfImplBuilder;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for hot config implementations
 */
public abstract class AbstractConfigFactory {
  /**
   * Gets config storage
   *
   * @return config storage
   */
  protected abstract ConfigStorage getConfigStorage();

  /**
   * Calculates config location for config interface
   *
   * @param configInterface config interface
   * @return config location
   */
  protected abstract <T> String configLocationFor(Class<T> configInterface);

  protected ConfContentSerializer getConfContentSerializer() {
    return new ConfAccessStdSerializer();
  }

  protected ConfAccess confAccess(Class<?> configInterface) {
    return new ConfAccess() {
      final String configLocation = configLocationFor(configInterface);

      @Override
      public ConfContent load() {
        try {
          return getConfContentSerializer().deserialize(getConfigStorage().loadConfigContent(configLocation));
        } catch (Exception e) {
          if (e instanceof RuntimeException) throw (RuntimeException) e;
          throw new RuntimeException(e);
        }
      }

      @Override
      public void write(ConfContent confContent) {
        try {
          getConfigStorage().saveConfigContent(configLocation, getConfContentSerializer().serialize(confContent));
        } catch (Exception e) {
          if (e instanceof RuntimeException) throw (RuntimeException) e;
          throw new RuntimeException(e);
        }
      }

      @Override
      public Date lastModifiedAt() {
        try {
          return getConfigStorage().getLastChangedAt(configLocation);
        } catch (Exception e) {
          if (e instanceof RuntimeException) throw (RuntimeException) e;
          throw new RuntimeException(e);
        }
      }
    };

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

  private final ConcurrentHashMap<Class<?>, Object> proxyMap = new ConcurrentHashMap<>();

  /**
   * Creates and returns instance of config
   *
   * @param configInterface config interface
   * @return instance of config
   */
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
        T ret = ConfImplBuilder.confImplBuilder(configInterface, confAccess(configInterface))
                               .currentTimeMillis(this::currentTimeMillis)
                               .changeCheckTimeoutMs(autoResetTimeout())
                               .build();

        proxyMap.put(configInterface, ret);
        return ret;
      }
    }
  }

  protected long currentTimeMillis() {
    return System.currentTimeMillis();
  }

}
