package kz.greetgo.conf.hot;

import java.util.Date;
import java.util.Map;

public class ConfigDataLoader {

  static void loadConfigDataTo(Map<String, Object> target,
                               HotConfigDefinition configDefinition,
                               ConfigStorage configStorage, Date now) {

    try {
      new ConfigDataLoader(target, configDefinition, configStorage, now).load();
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }

  }

  private final Map<String, Object> target;
  private final HotConfigDefinition configDefinition;
  private final ConfigStorage configStorage;
  private final LoadingLines loadingLines;

  private ConfigDataLoader(Map<String, Object> target, HotConfigDefinition configDefinition,
                           ConfigStorage configStorage, Date now) {
    this.target = target;
    this.configDefinition = configDefinition;
    this.configStorage = configStorage;
    loadingLines = new LoadingLines(now, configDefinition.description());
  }

  private void load() throws Exception {

    for (ElementDefinition ed : configDefinition.elementList()) {
      loadingLines.putDefinition(ed);
    }

    boolean contentExists = configStorage.isConfigContentExists(configDefinition.location());
    loadingLines.setContentExists(contentExists);
    if (contentExists) {
      for (String line : configStorage.loadConfigContent(configDefinition.location()).split("\n")) {
        loadingLines.readStorageLine(line);
      }
    }

    loadingLines.saveTo(target);

    if (loadingLines.needToSave()) {
      configStorage.saveConfigContent(configDefinition.location(), loadingLines.content());
    }
  }

}
