package kz.greetgo.conf.hot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static kz.greetgo.conf.ConfUtil.strToBool;
import static kz.greetgo.conf.hot.LoadingLines.killedLastEmptyLines;

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
  private final Date now;

  private ConfigDataLoader(Map<String, Object> target, HotConfigDefinition configDefinition,
                           ConfigStorage configStorage, Date now) {
    this.target = target;
    this.configDefinition = configDefinition;
    this.configStorage = configStorage;
    this.now = now;
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

  private void loadOld() throws Exception {

    final List<String> lines = new ArrayList<>();
    boolean isNew = true;
    if (configStorage.isConfigContentExists(configDefinition.location())) {
      isNew = false;
      Collections.addAll(lines, configStorage.loadConfigContent(configDefinition.location()).split("\n"));
    }

    Map<String, ElementDefinition> defValues = new LinkedHashMap<>();

    for (ElementDefinition ed : configDefinition.elementList()) {
      defValues.put(ed.name, ed);
    }

    for (String line : lines) {
      line = line.trim();

      int idx = line.indexOf('=');
      if (idx < 0) continue;

      String key = line.substring(0, idx).trim();
      String strValue = line.substring(idx + 1).trim();

      boolean commented = false;
      if (key.startsWith("#")) {
        key = key.substring(1).trim();
        if (key.startsWith("#")) continue;
        commented = true;
      }

      ElementDefinition hed = defValues.remove(key);
      if (hed == null) continue;

      if (commented) {
        target.put(key, hed.newDefaultValue());
      } else {
        target.put(key, parseStrValue(strValue, hed.typeManager.type(), hed.newDefaultValue()));
      }

    }

    if (defValues.size() == 0) return;

    killedLastEmptyLines(lines);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    lines.add("");
    lines.add("#");
    lines.add("# " + (isNew ? "Created" : "Added") + " at " + sdf.format(now));
    lines.add("#");

    if (isNew && configDefinition.description() != null) {
      for (String s : configDefinition.description().split("\n")) {
        lines.add("# " + s.trim());
      }
      lines.add("#");
    }

    for (Map.Entry<String, ElementDefinition> e : defValues.entrySet()) {
      lines.add("");
      String description = e.getValue().description;
      if (description != null) {
        for (String s : description.split("\n")) {
          lines.add("# " + s.trim());
        }
      }
      lines.add(e.getKey() + "=" + e.getValue().newDefaultValue());
      target.put(e.getKey(), e.getValue().newDefaultValue());
    }

    StringBuilder sb = new StringBuilder();
    for (String line : lines) {
      sb.append(line).append('\n');
    }

    configStorage.saveConfigContent(configDefinition.location(), sb.toString());
  }

  private static Object parseStrValue(String strValue, Class<?> type, Object defaultValue) {
    if (type == int.class || type == Integer.class) {
      try {
        return Integer.valueOf(strValue);
      } catch (NumberFormatException e) {
        return defaultValue;
      }
    }

    if (type == long.class || type == Long.class) {
      try {
        return Long.valueOf(strValue);
      } catch (NumberFormatException e) {
        return defaultValue;
      }
    }

    if (type == String.class) {
      return strValue;
    }

    if (type == boolean.class || type == Boolean.class) {
      return strToBool(strValue);
    }

    throw new IllegalArgumentException("Unknown type for config " + type);
  }
}
