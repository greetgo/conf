package kz.greetgo.conf.hot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigStorageForTests implements ConfigStorage {

  public final Map<String, String> contentMap = new HashMap<>();

  public final AtomicInteger callCountOfLoadConfigContent = new AtomicInteger(0);

  @Override
  public String loadConfigContent(String configLocation) throws Exception {
    callCountOfLoadConfigContent.incrementAndGet();
    if (!contentMap.containsKey(configLocation)) throw new RuntimeException("No content for " + configLocation);
    return contentMap.get(configLocation);
  }

  public final AtomicInteger callCountOfIsConfigContentExists = new AtomicInteger(0);

  @Override
  public boolean isConfigContentExists(String configLocation) throws Exception {
    callCountOfIsConfigContentExists.incrementAndGet();
    return contentMap.containsKey(configLocation);
  }

  public final AtomicInteger callCountOfSaveConfigContent = new AtomicInteger(0);

  @Override
  public void saveConfigContent(String configLocation, String configContent) throws Exception {
    callCountOfSaveConfigContent.incrementAndGet();
    contentMap.put(configLocation, configContent);
  }
}
