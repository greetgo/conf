package kz.greetgo.conf.hot;

import java.util.HashMap;
import java.util.Map;

public class ConfigStorageForTests implements ConfigStorage {

  public final Map<String, String> contentMap = new HashMap<>();

  @Override
  public String loadConfigContent(String configLocation) throws Exception {
    if (!contentMap.containsKey(configLocation)) throw new RuntimeException("No content for " + configLocation);
    return contentMap.get(configLocation);
  }

  @Override
  public boolean isConfigContentExists(String configLocation) throws Exception {
    return contentMap.containsKey(configLocation);
  }

  @Override
  public void saveConfigContent(String configLocation, String configContent) throws Exception {
    contentMap.put(configLocation, configContent);
  }
}
