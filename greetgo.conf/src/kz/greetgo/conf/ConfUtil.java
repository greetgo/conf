package kz.greetgo.conf;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfUtil {
  public static void readFromFile(Object readTo, File file) throws Exception {
    readFromStream(readTo, new FileInputStream(file));
  }

  public static void readFromFile(Object readTo, String fileName) throws Exception {
    readFromFile(readTo, new File(fileName));
  }

  public static void readFromStream(Object readTo, InputStream inputStream) throws Exception {
    if (readTo == null) {
      inputStream.close();
      return;
    }

    Class<?> class1 = readTo.getClass();

    ConfData cd = new ConfData();
    cd.readFromStream(inputStream);

    final Map<String, Method> setMethods = new HashMap<>();

    for (Method method : class1.getMethods()) {
      if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
        setMethods.put(method.getName(), method);
      }
    }

    FOR:
    for (String name : cd.list(null)) {
      {
        String setMethodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        Method method = setMethods.get(setMethodName);
        if (method != null) {
          method.invoke(readTo, convertToType(cd.str(name), method.getParameterTypes()[0]));
          continue FOR;
        }
      }
      try {
        Field field = class1.getField(name);
        field.set(readTo, convertToType(cd.str(name), field.getType()));
        continue FOR;
      } catch (NoSuchFieldException ignored) {
      }
    }
  }

  public static String readFile(File file) {
    try {
      try (FileInputStream in = new FileInputStream(file)) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte buf[] = new byte[1024 * 4];

        while (true) {
          int count = in.read(buf);
          if (count < 0) return bout.toString("UTF-8");
          bout.write(buf, 0, count);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeFile(File file, String content) {
    try (PrintStream out = new PrintStream(file, "UTF-8")) {
      out.print(content);
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private static final class PatternFormat {
    final Pattern pattern;
    final SimpleDateFormat format;

    public PatternFormat(Pattern pattern, SimpleDateFormat format) {
      this.pattern = pattern;
      this.format = format;
    }
  }

  private static final List<PatternFormat> PATTERN_FORMAT_LIST = new ArrayList<>();

  private static void addPatternFormat(String patternStr, String formatStr) {
    PATTERN_FORMAT_LIST.add(new PatternFormat(Pattern.compile(patternStr), new SimpleDateFormat(
        formatStr)));
  }

  static {
    addPatternFormat("(\\d{4}-\\d{2}-\\d{2})", "yyyy-MM-dd");
    addPatternFormat("(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2})", "yyyy-MM-dd HH:mm");
    addPatternFormat("(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})", "yyyy-MM-dd HH:mm:ss");
    addPatternFormat("(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3})",
        "yyyy-MM-dd HH:mm:ss.SSS");
    addPatternFormat("(\\d{4}-\\d{2}-\\d{2}/\\d{2}:\\d{2}:\\d{2}\\.\\d{3})",
        "yyyy-MM-dd/HH:mm:ss.SSS");
    addPatternFormat("(\\d{2}:\\d{2}:\\d{2}\\.\\d{3})", "HH:mm:ss.SSS");
    addPatternFormat("(\\d{2}:\\d{2}:\\d{2})", "HH:mm:ss");
    addPatternFormat("(\\d{2}:\\d{2})", "HH:mm");

    addPatternFormat("(\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3})",
        "dd/MM/yyyy HH:mm:ss.SSS");
    addPatternFormat("(\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2})", "dd/MM/yyyy HH:mm:ss");
    addPatternFormat("(\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2})", "dd/MM/yyyy HH:mm");
    addPatternFormat("(\\d{2}/\\d{2}/\\d{4})", "dd/MM/yyyy");

    addPatternFormat("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3})",
        "dd.MM.yyyy HH:mm:ss.SSS");
    addPatternFormat("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2}:\\d{2})", "dd.MM.yyyy HH:mm:ss");
    addPatternFormat("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})", "dd.MM.yyyy HH:mm");
    addPatternFormat("(\\d{2}\\.\\d{2}\\.\\d{4})", "dd.MM.yyyy");
  }

  public static Object convertToType(String str, Class<?> type) {
    if (type == null) return null;
    if (type.isAssignableFrom(String.class)) {
      return str;
    }
    if (type == Boolean.TYPE || type.isAssignableFrom(Boolean.class)) {
      if ("T".equalsIgnoreCase(str)) return true;
      if ("TRUE".equalsIgnoreCase(str)) return true;
      if ("ON".equalsIgnoreCase(str)) return true;
      if ("1".equalsIgnoreCase(str)) return true;
      if ("Y".equalsIgnoreCase(str)) return true;
      if ("YES".equalsIgnoreCase(str)) return true;
      return false;
    }
    if (type == Integer.TYPE || type.isAssignableFrom(Integer.class)) {
      if (str == null) return 0;
      return Integer.parseInt(str);
    }
    if (type == Long.TYPE || type.isAssignableFrom(Long.class)) {
      if (str == null) return 0L;
      return Long.parseLong(str);
    }
    if (type == Double.TYPE || type.isAssignableFrom(Double.class)) {
      if (str == null) return 0d;
      return Double.parseDouble(str);
    }
    if (type == Float.TYPE || type.isAssignableFrom(Float.class)) {
      if (str == null) return 0f;
      return Float.parseFloat(str);
    }
    if (type.isAssignableFrom(BigDecimal.class)) {
      if (str == null) return BigDecimal.ZERO;
      return new BigDecimal(str);
    }
    if (type.isAssignableFrom(Date.class)) {
      if (str == null) return null;
      for (PatternFormat pf : PATTERN_FORMAT_LIST) {
        Matcher m = pf.pattern.matcher(str);
        if (m.matches()) {
          try {
            return pf.format.parse(m.group(1));
          } catch (ParseException e) {
            throw new RuntimeException(e);
          }
        }
      }
      throw new IllegalArgumentException("Cannot detect date format for value " + str);
    }
    throw new IllegalArgumentException("Cannot convert to type " + type + " str value = " + str);
  }
}
