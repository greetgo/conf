package kz.greetgo.conf.hot;

import java.text.SimpleDateFormat;
import java.util.*;

public class AbstractConfigFactoryUtil {
  static void loadConfigDataTo(Map<String, Object> target,
                               HotConfigDefinition configDefinition,
                               ConfigStorage configStorage, Date now) {

    try {
      loadConfigDataToEx(target, configDefinition, configStorage, now);
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }

  }

  private static void loadConfigDataToEx(Map<String, Object> target,
                                         HotConfigDefinition configDefinition,
                                         ConfigStorage configStorage, Date now) throws Exception {

    final List<String> lines = new ArrayList<>();
    boolean isNew = true;
    if (configStorage.isConfigContentExists(configDefinition.location())) {
      isNew = false;
      Collections.addAll(lines, configStorage.loadConfigContent(configDefinition.location()).split("\n"));
    }

    Map<String, HotElementDefinition> defValues = new LinkedHashMap<>();

    for (HotElementDefinition hed : configDefinition) {
      defValues.put(hed.name, hed);
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

      HotElementDefinition hed = defValues.remove(key);
      if (hed == null) continue;

      if (commented) {
        target.put(key, hed.defaultValue);
      } else {
        target.put(key, parseStrValue(strValue, hed.type, hed.defaultValue));
      }

    }

    if (defValues.size() == 0) return;

    killedLastEmptyLines(lines);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    lines.add("");
    lines.add("#");
    lines.add("# " + (isNew ? "Created" : "Added") + " at " + sdf.format(now));
    lines.add("#");

    for (Map.Entry<String, HotElementDefinition> e : defValues.entrySet()) {
      lines.add("");
      String description = e.getValue().description;
      if (description != null) {
        for (String s : description.split("\n")) {
          lines.add("# " + s.trim());
        }
      }
      lines.add(e.getKey() + "=" + e.getValue().defaultValue);
      target.put(e.getKey(), e.getValue().defaultValue);
    }

    StringBuilder sb = new StringBuilder();
    for (String line : lines) {
      sb.append(line).append('\n');
    }

    configStorage.saveConfigContent(configDefinition.location(), sb.toString());
  }

  private static void killedLastEmptyLines(List<String> lines) {
    while (lines.size() > 0) {
      String lastLine = lines.get(lines.size() - 1).trim();
      if (lastLine.length() > 0) return;
      lines.remove(lines.size() - 1);
    }
  }

  private static Object parseStrValue(String strValue, Class<?> type, Object defaultValue) {
    if (type == int.class || type == Integer.class) {
      try {
        return Integer.valueOf(strValue);
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

  public static boolean strToBool(String str) {
    if (str == null) return false;

    str = str.trim().toUpperCase();

    if ("T".equals(str)) return true;
    if ("TRUE".equals(str)) return true;
    if ("ON".equals(str)) return true;
    if ("1".equals(str)) return true;
    if ("Y".equals(str)) return true;
    if ("YES".equals(str)) return true;
    if ("И".equals(str)) return true;
    if ("ИСТИНА".equals(str)) return true;
    if ("ДА".equals(str)) return true;
    if ("Д".equals(str)) return true;
    if ("是的".equals(str)) return true;

    return false;
  }
}
