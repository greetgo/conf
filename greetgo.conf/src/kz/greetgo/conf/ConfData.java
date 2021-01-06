package kz.greetgo.conf;

import kz.greetgo.conf.error.NoValue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This class is storing config data earlier read from file (or some other source)
 *
 * @author pompei
 */
public class ConfData {

  private final Map<String, List<Object>> data = new HashMap<>();

  /**
   * Performs config data access
   *
   * @return config data
   */
  public Map<String, List<Object>> getData() {
    return data;
  }

  /**
   * Reads data from file
   *
   * @param fileName full name of config file
   */
  public void readFromFile(String fileName) {
    readFromFile(new File(fileName));
  }

  /**
   * Reads data from file
   *
   * @param file reading file
   */
  public void readFromFile(File file) {
    try {
      try (FileInputStream in = new FileInputStream(file)) {
        readFromStream(in);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Reads data from byte array
   *
   * @param byteArray byte array
   */
  public void readFromByteArray(byte[] byteArray) {
    readFromStream(new ByteArrayInputStream(byteArray));
  }

  /**
   * Reads data from stream
   *
   * @param inputStream reading stream
   */
  public void readFromStream(InputStream inputStream) {
    try {
      readFromStream0(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Reads data from char sequence
   *
   * @param charSequence reading char sequence
   */
  public void readFromCharSequence(CharSequence charSequence) {
    readFromByteArray(charSequence.toString().getBytes(UTF_8));
  }

  @SuppressWarnings({"UnnecessaryLabelOnBreakStatement", "UnnecessaryLabelOnContinueStatement"})
  private void readFromStream0(InputStream inputStream) throws IOException {
    final LinkedList<Map<String, List<Object>>> stack = new LinkedList<>();
    stack.add(data);

    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
      WHILE:
      while (true) {
        String line = br.readLine();
        if (line == null) {
          break WHILE;
        }
        line = line.replaceAll("^\\s+", "");
        if (line.startsWith("#")) {
          continue WHILE;
        }
        if (line.length() == 0) {
          continue WHILE;
        }
        String[] pair = parseToPair(line);
        if (pair == null) {
          continue WHILE;
        }
        if (pair.length != 2) {
          continue WHILE;
        }

        if ("{".equals(pair[1])) {
          Map<String, List<Object>> hash = new HashMap<>();
          addValue(stack.getLast(), pair[0], hash);
          stack.add(hash);
          continue WHILE;
        }

        if ("}".equals(pair[0])) {
          stack.removeLast();
          continue WHILE;
        }

        addValue(stack.getLast(), pair[0], pair[1]);
      }
    }
  }

  private static void addValue(Map<String, List<Object>> map, String key, Object value) {
    map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
  }

  static String[] parseToPair(String line) {
    if (line == null) {
      return null;
    }
    {
      int idx1 = line.indexOf('=');
      int idx2 = line.indexOf(':');
      if (idx1 > -1 && (idx2 < 0 || idx1 < idx2)) {
        return new String[]{line.substring(0, idx1).trim(), line.substring(idx1 + 1).trim()};
      }
      if (idx2 > -1 && (idx1 < 0 || idx2 < idx1)) {
        return new String[]{line.substring(0, idx2).trim(), line.substring(idx2 + 1)};
      }
    }

    line = line.replaceAll("^\\s+", "");

    int idx = line.indexOf(' ');
    if (idx < 0) {
      String key = line.trim();
      if (key.length() == 0) return null;
      return new String[]{key, null};
    }

    {
      String value = line.substring(idx + 1).trim();
      if (value.length() == 0) value = null;
      return new String[]{line.substring(0, idx), value};
    }
  }

  /**
   * Read parameter value by name
   *
   * @param path parameter name-path
   * @return value of this parameter
   * @throws NoValue when no value for specified path
   */
  public String strEx(String path) {
    String[]                  split    = path.split("/");
    Map<String, List<Object>> cur      = data;
    StringBuilder             prevPath = new StringBuilder();
    for (int i = 0, C = split.length - 1; i < C; i++) {
      String step = split[i];
      if (prevPath.length() > 0) {
        prevPath.append('/');
      }
      prevPath.append(step);
      cur = getMap(cur, new Name(step), prevPath);
      if (cur == null) {
        throw new NoValue(prevPath);
      }
    }
    return getStr(cur, new Name(split[split.length - 1]), prevPath);
  }

  /**
   * Reads parameter value, if this parameter absents - returns default value
   *
   * @param path         parameter name-path
   * @param defaultValue default parameter value (it will be returned, if parameter absents)
   * @return value of parameter, or default value
   */
  public String str(String path, String defaultValue) {
    try {
      return strEx(path);
    } catch (NoValue ignore) {
      return defaultValue;
    }
  }

  /**
   * Reads parameter and convert it to int
   *
   * @param path name-path of parameter
   * @return int value of parameter
   */
  public int asInt(String path) {
    String str = str(path);
    if (str == null) return 0;
    return Integer.parseInt(str);
  }

  /**
   * <p>Reads parameter value, converts it to int and returns.</p>
   * <p>If this parameter absents in config file or parameter value cannot be parsed to int,
   * returns default value.</p>
   *
   * @param path         name-path of parameter
   * @param defaultValue default value of parameter
   * @return int value of parameter or default value
   */
  public int asInt(String path, int defaultValue) {
    String str = str(path);
    if (str == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException ignore) {
      return defaultValue;
    }
  }

  /**
   * Reads parameter and convert it to int, or throws exception if cannot do it
   *
   * @param path name-path of parameter
   * @return value of parameter converted to int, or default value
   * @throws NoValue               when no value for specified path
   * @throws NumberFormatException if the value does not contain a
   *                               parsable integer.
   */
  public int asIntEx(String path) {
    String str = strEx(path);
    return Integer.parseInt(str);
  }

  /**
   * Reads parameter and convert it to long
   *
   * @param path name-path of parameter
   * @return long value of parameter
   */
  public long asLong(String path) {
    String str = str(path);
    if (str == null) {
      return 0;
    }
    return Long.parseLong(str);
  }

  /**
   * <p>Reads parameter value, converts it to long and returns.</p>
   * <p>If this parameter absents in config file or parameter value cannot be parsed to long,
   * returns default value.</p>
   *
   * @param path         name-path of parameter
   * @param defaultValue default value of parameter
   * @return long value of parameter or default value
   */
  public long asLong(String path, long defaultValue) {
    String str = str(path);
    if (str == null) {
      return defaultValue;
    }
    try {
      return Long.parseLong(str);
    } catch (NumberFormatException ignore) {
      return defaultValue;
    }
  }

  /**
   * Reads parameter and convert it to long, or throws exception if cannot do it
   *
   * @param path name-path of parameter
   * @return value of parameter converted to long, or default value
   * @throws NoValue               when no value for specified path
   * @throws NumberFormatException if the value does not contain a
   *                               parsable integer.
   */
  public long asLongEx(String path) {
    String str = strEx(path);
    return Long.parseLong(str);
  }

  /**
   * <p>Reads parameter value as boolean (case is ignoring)
   * <table>
   * <tr><th>If parameter value is...</th><th>then returns...</th></tr>
   * <tr><td>1</td><td>true</td></tr>
   * <tr><td>ИСТИНА</td><td>true</td></tr>
   * <tr><td>ДА</td><td>true</td></tr>
   * <tr><td>Д</td><td>true</td></tr>
   * <tr><td>YES</td><td>true</td></tr>
   * <tr><td>Y</td><td>true</td></tr>
   * <tr><td>ON</td><td>true</td></tr>
   * <tr><td>TRUE</td><td>true</td></tr>
   * <tr><td>T</td><td>true</td></tr>
   * <tr><td>真相</td><td>true</td></tr>
   * <tr><td>&lt;otherwise&gt;</td><td>false</td></tr>
   * </table>
   * </p>
   * <p>If path to parameter is absent, returns <code>false</code></p>
   *
   * @param path path to parameter
   * @return true/false value
   */
  public boolean bool(String path) {
    return bool(path, false);
  }

  /**
   * <p>Reads parameter value as boolean or returns <code>defaultValue</code>
   * when path to parameter is absent</p>
   * <p>What value is <code>true</code> and what value is <code>false</code> see {@link #bool(String)}</p>
   *
   * @param path         path to parameter
   * @param defaultValue returning value when path to parameter is absent
   * @return <code>true</code> or <code>false</code>
   * @see #bool(String)
   */
  public boolean bool(String path, boolean defaultValue) {
    try {
      return boolEx(path);
    } catch (NoValue ignore) {
      return defaultValue;
    }
  }

  /**
   * <p>Reads parameter value as boolean or throws {@link NoValue} when path to parameter is absent</p>
   * <p>What value is <code>true</code> and what value is <code>false</code> see {@link #bool(String)}</p>
   *
   * @param path path to parameter
   * @return <code>true</code> or <code>false</code>
   * @throws NoValue if path to parameter is absent
   * @see #bool(String)
   */
  @SuppressWarnings("RedundantIfStatement")
  public boolean boolEx(String path) {
    String str = strEx(path);
    if (str == null) return false;
    str = str.trim().toUpperCase();
    if (str.length() == 0) return false;
    {
      if (str.equals("1")) return true;

      if (str.equals("ИСТИНА")) return true;
      if (str.equals("TRUE")) return true;
      if (str.equals("YES")) return true;
      if (str.equals("ON")) return true;
      if (str.equals("ДА")) return true;
      if (str.equals("T")) return true;
      if (str.equals("Д")) return true;
      if (str.equals("Y")) return true;

      if (str.equals("真相")) return true;
    }
    return false;
  }

  /**
   * Calls {@link #dateEx(String, String...)} with formats =
   * <br>"yyyy-MM-dd'T'HH:mm:ss.SSS",
   * <br>"yyyy-MM-dd'T'HH:mm:ss",
   * <br>"yyyy-MM-dd'T'HH:mm",
   * <br>"yyyy-MM-dd HH:mm:ss.SSS",
   * <br>"yyyy-MM-dd HH:mm:ss",
   * <br>"yyyy-MM-dd HH:mm",
   * <br>"yyyy-MM-dd",
   * <br>"dd/MM/yyyy HH:mm:ss.SSS",
   * <br>"dd/MM/yyyy HH:mm:ss",
   * <br>"dd/MM/yyyy HH:mm",
   * <br>"dd/MM/yyyy",
   * <br>"HH:mm:ss.SSS",
   * <br>"HH:mm:ss",
   * <br>"HH:mm"
   *
   * @param path path to parameter
   * @return date value
   * @throws NoValue if path to parameters is absent
   * @throws NoValue if parameter value cannot be converted to date with specified formats
   */
  public Date dateEx(String path) {
    return dateEx(path,
                  "yyyy-MM-dd'T'HH:mm:ss.SSS",
                  "yyyy-MM-dd'T'HH:mm:ss",
                  "yyyy-MM-dd'T'HH:mm",
                  "yyyy-MM-dd HH:mm:ss.SSS",
                  "yyyy-MM-dd HH:mm:ss",
                  "yyyy-MM-dd HH:mm",
                  "yyyy-MM-dd",
                  "dd/MM/yyyy HH:mm:ss.SSS",
                  "dd/MM/yyyy HH:mm:ss",
                  "dd/MM/yyyy HH:mm",
                  "dd/MM/yyyy",
                  "HH:mm:ss.SSS",
                  "HH:mm:ss",
                  "HH:mm"
    );
  }

  /**
   * <p>Reads value of parameter and converts it to date with specified formats</p>
   * <p>Each argument in array <code>formats</code> may contain some formats delimited with comma</p>
   * <p>For converting, it is using class {@link SimpleDateFormat}</p>
   * <p>If trimmed parameter value starts with <code>#</code>, returns <code>null</code></p>
   * <p>If trimmed parameter value is empty, returns <code>null</code></p>
   *
   * @param path    path to parameter
   * @param formats formats of date; each format may contain some formats delimited with comma
   * @return date value or {@link NoValue} if no path to parameter of cannot convert parameter value to date by
   * specified formats
   * @throws NoValue if path to parameters is absent
   * @throws NoValue if parameter value cannot be converted to date with specified formats
   */
  @SuppressWarnings("UnnecessaryContinue")
  public Date dateEx(String path, String... formats) {

    String strValue = strEx(path);
    if (strValue == null) {
      return null;
    }
    strValue = strValue.trim();
    if (strValue.startsWith("#")) {
      return null;
    }

    List<SimpleDateFormat> formatList = new LinkedList<>();
    for (String format : formats) {
      if (format == null) {
        continue;
      }
      for (String f : format.split(";")) {
        String trimmedFormat = f.trim();
        if (trimmedFormat.length() == 0) {
          continue;
        }
        formatList.add(new SimpleDateFormat(trimmedFormat));
      }
    }

    for (SimpleDateFormat sdf : formatList) {

      try {
        return sdf.parse(strValue);
      } catch (ParseException e) {
        continue;
      }

    }

    throw new NoValue(path);
  }

  /**
   * <p>Reads value of parameter as date with formats described in method {@link #dateEx(String)}</p>
   * <p>If no parameter or parameter value cannot be parsed, returns <code>null</code></p>
   *
   * @param path path to parameter
   * @return date value or <code>null</code>
   */
  public Date date(String path) {
    return date(path, (Date) null);
  }

  /**
   * <p>Reads value of parameter as date with formats specified in <code>formats</code></p>
   * <p>If no parameter or parameter value cannot be parsed, returns <code>null</code></p>
   *
   * @param path    path to parameter
   * @param formats list of formats. Each element can contains several formats delimited with comma
   * @return date value or <code>null</code>
   */
  public Date date(String path, String... formats) {
    return date(path, null, formats);
  }

  /**
   * <p>Reads value of parameter as date with formats described in method {@link #dateEx(String)}</p>
   * <p>If no parameter or parameter value cannot be parsed, returns <code>defaultValue</code></p>
   *
   * @param path         path to parameter
   * @param defaultValue returning value when no parameter or parameter value cannot be parsed to date
   * @return date value or <code>defaultValue</code>
   */
  @SuppressWarnings("RedundantIfStatement")
  public Date date(String path, Date defaultValue) {
    try {
      Date ret = dateEx(path);
      if (ret == null) return null;
      return ret;
    } catch (NoValue ignore) {
      return defaultValue;
    }
  }

  /**
   * <p>Reads value of parameter as date with formats specified in <code>formats</code></p>
   * <p>If no parameter or parameter value cannot be parsed, returns <code>defaultValue</code></p>
   *
   * @param path         path to parameter
   * @param defaultValue returning value when no parameter or parameter value cannot be parsed to date
   * @param formats      list of formats. Each element can contains several formats delimited with comma
   * @return date value or <code>defaultValue</code>
   */
  public Date date(String path, Date defaultValue, String... formats) {
    try {
      Date ret = dateEx(path, formats);
      if (ret == null) {
        return defaultValue;
      }
      return ret;
    } catch (NoValue ignore) {
      return defaultValue;
    }
  }

  /**
   * Reads parameter value, if this parameter absents - returns <code>null</code>
   *
   * @param path parameter name-path
   * @return value of parameter, or <code>null</code>
   */
  public String str(String path) {
    try {
      return strEx(path);
    } catch (NoValue e) {
      return null;
    }
  }


  private static class Name {
    private static final Pattern END_DIGIT = Pattern.compile("^(.+)\\.(\\d+)$");

    public final int    index;
    public final String name;

    public Name(String bigName) {
      if (bigName == null) {
        index = 0;
        name  = null;
        return;
      }

      Matcher m = END_DIGIT.matcher(bigName);
      if (!m.matches()) {
        index = 0;
        name  = bigName;
        return;
      }

      index = Integer.parseInt(m.group(2));
      name  = m.group(1);
    }

    public String bigName() {
      if (index == 0) {
        return name;
      }
      return name + '.' + index;
    }

    @Override
    public String toString() {
      return "name = " + name + ", index = " + index;
    }
  }

  private String getStr(Map<String, List<Object>> map, Name name, StringBuilder prevPath) {
    List<Object> list = map.get(name.name);
    if (list == null) {
      throw new NoValue(prevPath, name.bigName());
    }

    int index = 0;
    for (Object object : list) {
      if (object instanceof String) {
        if (index == name.index) return (String) object;
        index++;
      }
    }

    {
      if (prevPath.length() > 0) prevPath.append("/");
      prevPath.append(name.bigName());
      throw new NoValue(prevPath);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Map<String, List<Object>> getMap(Map<String, List<Object>> map, Name name,
                                           StringBuilder prevPath) {
    List<Object> list = map.get(name.name);
    if (list == null) return null;

    int index = 0;
    for (Object object : list) {
      if (object instanceof Map) {
        if (index == name.index) return (Map) object;
        index++;
      }
    }

    if (prevPath != null) {
      if (prevPath.length() > 0) prevPath.append("/");
      prevPath.append(name.bigName());
      throw new NoValue("No such map index for " + prevPath);
    }
    return null;
  }

  public List<String> list(String path) {
    List<String>              ret = new ArrayList<>();
    Map<String, List<Object>> cur = data;
    if (path != null && path.length() > 0) {
      for (String name : path.split("/")) {
        cur = getMap(cur, new Name(name), null);
        if (cur == null) return ret;
      }
    }
    ret.addAll(cur.keySet());
    return ret;
  }

}
