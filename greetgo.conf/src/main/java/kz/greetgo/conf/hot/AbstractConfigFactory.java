package kz.greetgo.conf.hot;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static kz.greetgo.conf.hot.AbstractConfigFactoryUtil.loadConfigDataTo;

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
  public void reset() {
    for (HotConfigImpl mediator : workingConfigs.values()) {
      mediator.data.set(null);
    }
  }

  private final Map<String, HotConfigImpl> workingConfigs = new ConcurrentHashMap<>();

  private class HotConfigImpl implements HotConfig {
    private final AtomicReference<Map<String, Object>> data = new AtomicReference<>(null);
    private final HotConfigDefinition configDefinition;

    public HotConfigImpl(HotConfigDefinition configDefinition) {
      this.configDefinition = configDefinition;
    }

    Map<String, Object> getData() {
      {
        Map<String, Object> x = data.get();
        if (x != null) return x;
      }

      synchronized (this) {
        {
          Map<String, Object> x = data.get();
          if (x != null) return x;
        }
        {
          Map<String, Object> newData = new HashMap<>();
          loadConfigDataTo(newData, configDefinition, getConfigStorage(), new Date());
          newData = Collections.unmodifiableMap(newData);
          data.set(newData);
          return newData;
        }
      }
    }

    @Override
    public Object getElementValue(String elementName) {
      return getData().get(elementName);
    }

    @Override
    public boolean isElementExists(String elementName) {
      return getData().containsKey(elementName);
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
      if (config != null) return config;
    }
    synchronized (this) {
      {
        HotConfigImpl config = workingConfigs.get(configDefinition.location());
        if (config != null) return config;
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
    value = value.replaceAll("\\{user\\.name\\}", System.getProperty("user.name"));
    value = value.replaceAll("\\{user\\.home\\}", System.getProperty("user.home"));
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
      if (hotConfig != null) return createInvocationHandlerOnHotConfig(hotConfig);
    }

    return createInvocationHandlerOnHotConfig(
        getOrCreateConfig(createHotConfigDefinition(configLocation, configInterface))
    );
  }

  private InvocationHandler createInvocationHandlerOnHotConfig(final HotConfig hotConfig) {
    return new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return hotConfig.getElementValue(method.getName());
      }
    };
  }

  private <T> HotConfigDefinition createHotConfigDefinition(String configLocation, Class<T> configInterface) {
    List<HotElementDefinition> elementDefinitions = new ArrayList<>();
    for (Method method : configInterface.getDeclaredMethods()) {
      elementDefinitions.add(createHotElementDefinition(method));
    }
    return new HotConfigDefinitionModel(configLocation, extractDescription(configInterface), elementDefinitions);
  }

  private <T> String extractDescription(Class<T> configInterface) {
    Description a = configInterface.getAnnotation(Description.class);
    if (a == null) return null;
    return a.value();
  }

  private HotElementDefinition createHotElementDefinition(Method method) {
    String name = method.getName();
    Class<?> type = method.getReturnType();
    Object defaultValue = extractDefaultValue(method);
    String description = extractDescription(method);
    return new HotElementDefinition(name, type, defaultValue, description);
  }

  private String extractDescription(Method method) {
    Description a = method.getAnnotation(Description.class);
    if (a == null) return null;
    return a.value();
  }

  private Object extractDefaultValue(Method method) {
    {
      DefaultStrValue a = method.getAnnotation(DefaultStrValue.class);
      if (a != null) return replaceParametersInDefaultStrValue(a.value());
    }
    {
      DefaultIntValue a = method.getAnnotation(DefaultIntValue.class);
      if (a != null) return a.value();
    }
    {
      DefaultBoolValue a = method.getAnnotation(DefaultBoolValue.class);
      if (a != null) return a.value();
    }
    {
      Class<?> returnType = method.getReturnType();
      if (returnType == int.class) return 0;
      if (returnType == long.class) return 0L;
      if (returnType == boolean.class) return false;
    }
    return null;
  }
}
