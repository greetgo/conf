package kz.greetgo.conf.spring.cloud.hot;

import kz.greetgo.conf.hot.AbstractConfigFactory;
import kz.greetgo.conf.hot.HotConfig;
import kz.greetgo.conf.hot.HotConfigDefinition;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.unmodifiableMap;
import static kz.greetgo.conf.spring.cloud.hot.CloudConfigDataLoader.loadConfigDataToCloud;

/**
 * Factory for hot config implementations
 */
public abstract class AbstractCloudConfigFactory extends AbstractConfigFactory {

  private class HotConfigImpl extends AbstractConfigFactory.HotConfigImpl {
    private final AtomicReference<Map<String, Object>> cloudData = new AtomicReference<>(null);

    public HotConfigImpl(HotConfigDefinition configDefinition) {
      super(configDefinition);
    }

    @Override
    protected void reset() {
      cloudData.set(null);
      data.set(null);
    }

    Map<String, Object> getCloudData() {
      preReset();

      {
        Map<String, Object> x = cloudData.get();
        if (x != null) {
          return x;
        }
      }

      synchronized (workingConfigs) {
        {
          Map<String, Object> x = cloudData.get();
          if (x != null) {
            return x;
          }
        }
        {
          Map<String, Object> newData = new HashMap<>();
          loadConfigDataToCloud(newData, configDefinition, getConfigStorage(), new Date());
          newData = unmodifiableMap(newData);
          cloudData.set(newData);
          return newData;
        }
      }
    }

    @Override
    public Object getElementValue(String elementName) {
      Object envValue = getCloudData().get(configDefinition.configInterface().getSimpleName() + "." + elementName);

      if (envValue != null) return envValue;

      return super.getElementValue(elementName);
    }
  }

  /**
   * Creates and returns instance of hot config
   *
   * @param configDefinition definition of config
   * @return instance of hot config
   */
  @Override
  public HotConfig getOrCreateConfig(HotConfigDefinition configDefinition) {
    {
      AbstractCloudConfigFactory.HotConfigImpl config = (HotConfigImpl) workingConfigs.get(configDefinition.location());
      if (config != null) {
        return config;
      }
    }
    synchronized (this) {
      {
        AbstractCloudConfigFactory.HotConfigImpl config = (HotConfigImpl) workingConfigs.get(configDefinition.location());
        if (config != null) {
          return config;
        }
      }

      AbstractCloudConfigFactory.HotConfigImpl config = new AbstractCloudConfigFactory.HotConfigImpl(configDefinition);
      workingConfigs.put(configDefinition.location(), config);

      return config;
    }
  }

}
