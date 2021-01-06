package kz.greetgo.conf.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculateSizes {

  private static final Pattern END    = Pattern.compile("\\.(\\d+)$");
  private static final Pattern MIDDLE = Pattern.compile("\\.(\\d+)\\.");

  private CalculateSizes() {}

  private final Map<String, Integer> result = new HashMap<>();

  public static Map<String, Integer> of(Set<String> paramKeys) {
    return new CalculateSizes().calculate(paramKeys);
  }

  private Map<String, Integer> calculate(Set<String> paramKeys) {
    for (String paramKey : paramKeys) {

      {
        Matcher m = END.matcher(paramKey);
        if (m.find()) {
          String key   = paramKey.substring(0, m.start());
          int    value = Integer.parseInt(m.group(1));
          append(key, value);
        }
      }

      {
        Matcher m = MIDDLE.matcher(paramKey);
        while (m.find()) {
          String key   = paramKey.substring(0, m.start());
          int    value = Integer.parseInt(m.group(1));
          append(key, value);
        }
      }

    }

    return result;
  }

  private void append(String key, int value) {
    Integer current = result.get(key);
    if (current == null || current <= value) {
      result.put(key, value + 1);
    }
  }
}
