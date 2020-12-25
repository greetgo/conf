package kz.greetgo.conf.hot;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigStorageForTests implements ConfigStorage {

  private final Map<String, String> contentMap = new ConcurrentHashMap<>();
  private final Map<String, Date> changeMap = new ConcurrentHashMap<>();

  private final AtomicInteger callCountOfLoadConfigContent = new AtomicInteger(0);

  public int callCountOfLoadConfigContent() {
    return callCountOfLoadConfigContent.get();
  }

  @Override
  public String loadConfigContent(String configLocation) {
    callCountOfLoadConfigContent.incrementAndGet();
    if (!contentMap.containsKey(configLocation)) {
      throw new RuntimeException("No content for " + configLocation);
    }
    return contentMap.get(configLocation);
  }

  private final AtomicInteger callCountOfIsConfigContentExists = new AtomicInteger(0);

  public int callCountOfIsConfigContentExists() {
    return callCountOfIsConfigContentExists.get();
  }

  @Override
  public boolean isConfigContentExists(String configLocation) {
    callCountOfIsConfigContentExists.incrementAndGet();
    return contentMap.containsKey(configLocation);
  }

  private final AtomicInteger callCountOfSaveConfigContent = new AtomicInteger(0);

  public int callCountOfSaveConfigContent() {
    return callCountOfSaveConfigContent.get();
  }

  @Override
  public void saveConfigContent(String configLocation, String configContent) {
    callCountOfSaveConfigContent.incrementAndGet();
    contentMap.put(configLocation, configContent);
    changeMap.put(configLocation, new Date());
  }

  @Override
  public Date getLastChangedAt(String configLocation) {
    return changeMap.get(configLocation);
  }

}
