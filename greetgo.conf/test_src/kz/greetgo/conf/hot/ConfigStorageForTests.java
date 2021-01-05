package kz.greetgo.conf.hot;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigStorageForTests implements ConfigStorage {

  private final Map<String, String> contentMap = new ConcurrentHashMap<>();
  private final Map<String, Date>   changeMap  = new ConcurrentHashMap<>();

  private final AtomicInteger callCountOfLoadConfigContent = new AtomicInteger(0);

  public int callCountOfLoad() {
    return callCountOfLoadConfigContent.get();
  }

  @Override
  public String loadConfigContent(String configLocation) {
    callCountOfLoadConfigContent.incrementAndGet();
    if (!contentMap.containsKey(configLocation)) {
      return null;
    }
    return contentMap.get(configLocation);
  }

  private final AtomicInteger callCountOfIsConfigContentExists = new AtomicInteger(0);

  public int callCountOfIsExists() {
    return callCountOfIsConfigContentExists.get();
  }

  @Override
  public boolean isConfigContentExists(String configLocation) {
    callCountOfIsConfigContentExists.incrementAndGet();
    return contentMap.containsKey(configLocation);
  }

  public Set<String> configLocations() {
    return contentMap.keySet();
  }

  private final AtomicInteger callCountOfSaveConfigContent = new AtomicInteger(0);

  public int callCountOfSave() {
    return callCountOfSaveConfigContent.get();
  }

  @Override
  public void saveConfigContent(String configLocation, String configContent) {
    callCountOfSaveConfigContent.incrementAndGet();
    contentMap.put(configLocation, configContent);
    changeMap.put(configLocation, new Date());
  }

  private final AtomicInteger callCountOfGetLastChangedAt = new AtomicInteger(0);

  public int callCountOfGetLastChangedAt() {
    return callCountOfGetLastChangedAt.get();
  }

  @Override
  public Date getLastChangedAt(String configLocation) {
    callCountOfGetLastChangedAt.incrementAndGet();
    return changeMap.get(configLocation);
  }

  public String getContent(String configLocation) {
    return contentMap.get(configLocation);
  }
}
