package kz.greetgo.conf.hot;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static java.util.Collections.unmodifiableMap;
import static kz.greetgo.conf.hot.ConfigDataLoader.loadConfigDataTo;

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

  /**
   * Marks all configs to reread from storage
   */
  public void resetAll() {
    resetIf(config -> true);
  }

  /**
   * Marks specified configs to reread from storage
   */
  public void resetIf(Predicate<HotConfig> predicate) {
    for (HotConfigImpl mediator : workingConfigs.values()) {
      if (predicate.test(mediator)) {
        mediator.reset();
      }
    }
  }

  private static final Object ABSENT_ENV = new Object();

  /**
   * Defines auto reset timeout. It is a time interval in milliseconds to check last config modification date and time.
   * And if the date and time was changed, then it calls method `reset` for this config.
   *
   * @return auto reset timeout. Zero - auto resetting is off
   */
  protected long autoResetTimeout() {
    return 500;
  }

  private final Map<String, HotConfigImpl> workingConfigs = new ConcurrentHashMap<>();

  private class HotConfigImpl implements HotConfig {
    private final AtomicReference<Map<String, Object>> data = new AtomicReference<>(null);
    private final HotConfigDefinition configDefinition;

    public HotConfigImpl(HotConfigDefinition configDefinition) {
      this.configDefinition = configDefinition;
    }

    void reset() {
      data.set(null);
    }

    private final AtomicReference<Date> lastModificationTime = new AtomicReference<>(null);
    private final AtomicLong lastChecked = new AtomicLong(System.currentTimeMillis());

    private void preReset() {
      try {

        long autoResetTimeout = autoResetTimeout();

        if (autoResetTimeout == 0) {
          return;
        }

        long delay = System.currentTimeMillis() - lastChecked.get();

        if (delay < autoResetTimeout) {
          return;
        }

        Date lastModificationTime = this.lastModificationTime.get();
        if (lastModificationTime == null) {
          Date lastChangedAt = getConfigStorage().getLastChangedAt(configDefinition.location());
          this.lastModificationTime.set(lastChangedAt);
          lastChecked.set(System.currentTimeMillis());
          return;
        }

        Date lastChangedAt = getConfigStorage().getLastChangedAt(configDefinition.location());
        lastChecked.set(System.currentTimeMillis());
        if (lastChangedAt == null) {
          return;
        }

        if (lastChangedAt.equals(lastModificationTime)) {
          return;
        }

        this.lastModificationTime.set(lastChangedAt);

        reset();

      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    Map<String, Object> getData() {
      preReset();

      {
        Map<String, Object> x = data.get();
        if (x != null) {
          return x;
        }
      }

      synchronized (workingConfigs) {
        {
          Map<String, Object> x = data.get();
          if (x != null) {
            return x;
          }
        }
        {
          Map<String, Object> newData = new HashMap<>();
          loadConfigDataTo(newData, configDefinition, getConfigStorage(), new Date());
          newData = unmodifiableMap(newData);
          data.set(newData);
          return newData;
        }
      }
    }


    private final ConcurrentHashMap<String, Object> environmentValues = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Object getElementValue(String elementName) {

      Object envValue = environmentValues.get(elementName);

      if (envValue == null) {
        for (ElementDefinition definition : configDefinition.elementList()) {
          if (definition.name.equals(elementName)) {
            if (definition.firstReadEnv == null) {
              envValue = ABSENT_ENV;
              break;
            }

            String strEnvValue = System.getenv(definition.firstReadEnv.value());
            if (strEnvValue != null && strEnvValue.trim().length() > 0) {
              envValue = definition.typeManager.fromStr(strEnvValue);
            } else {
              envValue = ABSENT_ENV;
            }

            break;
          }
        }
        if (envValue == null) {
          envValue = ABSENT_ENV;
        }
        environmentValues.put(elementName, envValue);
      }

      if (envValue == ABSENT_ENV) {
        return getData().get(elementName);
      }

      return envValue;
    }

    @Override
    public boolean isElementExists(String elementName) {
      return getData().containsKey(elementName);
    }

    @Override
    public String location() {
      return configDefinition.location();
    }

    @Override
    public Class<?> configInterface() {
      return configDefinition.configInterface();
    }
  }

  /**
   * Creates and returns instance of hot config
   *
   * @param configDefinition definition of config
   * @return instance of hot config
   */
  public HotConfig getOrCreateConfig(HotConfigDefinition configDefinition) {
    {
      HotConfigImpl config = workingConfigs.get(configDefinition.location());
      if (config != null) {
        return config;
      }
    }
    synchronized (this) {
      {
        HotConfigImpl config = workingConfigs.get(configDefinition.location());
        if (config != null) {
          return config;
        }
      }

      HotConfigImpl config = new HotConfigImpl(configDefinition);
      workingConfigs.put(configDefinition.location(), config);

      return config;
    }
  }

  /**
   * Creates and returns instance of config
   *
   * @param configInterface config interface
   * @return instance of config
   */
  @SuppressWarnings("unchecked")
  public <T> T createConfig(Class<T> configInterface) {
    return (T) Proxy.newProxyInstance(
      getClass().getClassLoader(), new Class<?>[]{configInterface}, getInvocationHandler(configInterface)
    );
  }


  /**
   * Replace parameters in <code>value</code> with what you want and return it
   *
   * @param value value containing parameters
   * @return value without parameters - all parameters was replaced with it's values
   */
  protected String replaceParametersInDefaultStrValue(String value) {
    value = value.replaceAll("\\{user\\.name}", System.getProperty("user.name"));
    value = value.replaceAll("\\{user\\.home}", System.getProperty("user.home"));
    return value;
  }

  private final Map<Class<?>, InvocationHandler> configInterfaceInvocationHandlerMap = new HashMap<>();

  private <T> InvocationHandler getInvocationHandler(Class<T> configInterface) {
    {
      InvocationHandler x = configInterfaceInvocationHandlerMap.get(configInterface);
      if (x != null) return x;
    }
    synchronized (this) {
      {
        InvocationHandler x = configInterfaceInvocationHandlerMap.get(configInterface);
        if (x != null) return x;
      }

      InvocationHandler x = createInvocationHandlerFor(configInterface);
      configInterfaceInvocationHandlerMap.put(configInterface, x);
      return x;
    }
  }

  private <T> InvocationHandler createInvocationHandlerFor(Class<T> configInterface) {
    String configLocation = configLocationFor(configInterface);

    {
      HotConfig hotConfig = workingConfigs.get(configLocation);
      if (hotConfig != null) {
        return createInvocationHandlerOnHotConfig(hotConfig, configInterface);
      }
    }

    return createInvocationHandlerOnHotConfig(
      getOrCreateConfig(

        DefinitionCreator.createDefinition(
          configLocation, configInterface, this::replaceParametersInDefaultStrValue
        )

      ), configInterface
    );
  }

  private InvocationHandler createInvocationHandlerOnHotConfig(final HotConfig hotConfig,
                                                               final Class<?> configInterface) {
    return (proxy, method, args) -> {

      if (method.getParameterTypes().length > 0) return null;

      if ("toString".equals(method.getName())) {
        return "[Hot config for <" + configInterface.getName() + ">@" + System.identityHashCode(this) + "]";
      }

      return hotConfig.getElementValue(method.getName());
    };
  }
}
