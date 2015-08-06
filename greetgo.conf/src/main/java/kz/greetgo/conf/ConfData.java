package kz.greetgo.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Хранит данные конфигурации, считанные из конфигурационного файла
 * 
 * @author pompei
 */
public class ConfData {
  private final Map<String, List<Object>> data = new HashMap<>();
  
  /**
   * Возвращает сами данные из конфигурационного файла
   * 
   * @return данные из конфигурационного файла
   */
  public Map<String, List<Object>> getData() {
    return data;
  }
  
  /**
   * Чтение данных из конфигурационного файла
   * 
   * @param fileName
   *          имя читаемого файла
   * @throws Exception
   *           для проброса исключений, чтобы не загромождать код try/catch-ми
   */
  public void readFromFile(String fileName) throws Exception {
    readFromFile(new File(fileName));
  }
  
  /**
   * Чтение данных из конфигурационного файла
   * 
   * @param file
   *          читаемый файл
   * @throws Exception
   *           для проброса исключений, чтобы не загромождать код try/catch-ми
   */
  public void readFromFile(File file) throws Exception {
    try (FileInputStream in = new FileInputStream(file)) {
      readFromStream(in);
    }
  }
  
  /**
   * Чтение данных из потока
   * 
   * @param inputStream
   *          читаемый поток
   * @throws Exception
   *           для проброса исключений, чтобы не загромождать код try/catch-ми
   */
  public void readFromStream(InputStream inputStream) throws Exception {
    final LinkedList<Map<String, List<Object>>> stack = new LinkedList<>();
    stack.add(data);
    
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    String line = null;
    WHILE: while ((line = br.readLine()) != null) {
      line = line.replaceAll("^\\s+", "");
      if (line.startsWith("#")) continue WHILE;
      if (line.length() == 0) continue WHILE;
      String pair[] = parseToPair(line);
      if (pair == null) continue WHILE;
      if (pair.length != 2) continue WHILE;
      
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
    br.close();
  }
  
  private static void addValue(Map<String, List<Object>> map, String key, Object value) {
    List<Object> list = map.get(key);
    if (list == null) {
      list = new ArrayList<>();
      map.put(key, list);
    }
    
    list.add(value);
  }
  
  static String[] parseToPair(String line) {
    if (line == null) return null;
    {
      int idx1 = line.indexOf('=');
      int idx2 = line.indexOf(':');
      if (idx1 > -1 && (idx2 < 0 || idx1 < idx2)) {
        return new String[] { line.substring(0, idx1).trim(), line.substring(idx1 + 1).trim() };
      }
      if (idx2 > -1 && (idx1 < 0 || idx2 < idx1)) {
        return new String[] { line.substring(0, idx2).trim(), line.substring(idx2 + 1) };
      }
    }
    
    line = line.replaceAll("^\\s+", "");
    
    int idx = line.indexOf(' ');
    if (idx < 0) {
      String key = line.trim();
      if (key.length() == 0) return null;
      return new String[] { key, null };
    }
    
    {
      String value = line.substring(idx + 1).trim();
      if (value.length() == 0) value = null;
      return new String[] { line.substring(0, idx), value };
    }
  }
  
  /**
   * Получение параметра по имени
   * 
   * @param path
   *          имя переменной (может содержать слэши и точки)
   * @return значение читаемого параметра
   */
  public String strEx(String path) {
    String[] split = path.split("/");
    Map<String, List<Object>> cur = data;
    StringBuilder prevPath = new StringBuilder();
    for (int i = 0, C = split.length - 1; i < C; i++) {
      String step = split[i];
      if (prevPath.length() > 0) prevPath.append('/');
      prevPath.append(step);
      cur = getMap(cur, new Name(step), prevPath);
      if (cur == null) throw new IllegalArgumentException("No map in " + prevPath);
    }
    return getStr(cur, new Name(split[split.length - 1]), prevPath);
  }
  
  /**
   * Читает параметр с указанием значения по умолчанию, которое будет использоваться, если этого
   * параметра нет в файле конфигурации
   * 
   * @param path
   *          имя переменной (может содержать слэши и точки)
   * @param defaultValue
   *          значение по умолчанию
   * @return прочитанное значение или значение по умолчанию, если этого параметра нет в
   *         конфигурационного файла
   */
  public String str(String path, String defaultValue) {
    try {
      return strEx(path);
    } catch (Exception e) {
      return defaultValue;
    }
  }
  
  /**
   * Читает параметр как целое число
   * 
   * @param path
   *          имя переменной (может содержать слэши и точки)
   * @return значение параметра
   */
  public int inte(String path) {
    String str = str(path);
    if (str == null) return 0;
    return Integer.parseInt(str);
  }
  
  /**
   * Читает параметр как целое число с возможностью указания значения по умолчанию, если такого
   * параметра нет в файле
   * 
   * @param path
   *          имя переменной (может содержать слэши и точки)
   * @param defaultValue
   *          значение по умолчанию
   * @return прочитанное значение или значение по умолчанию, если этого параметра нет в
   *         конфигурационного файла
   */
  public int inte(String path, int defaultValue) {
    String str = str(path);
    if (str == null) return defaultValue;
    return Integer.parseInt(str);
  }
  
  public String str(String path) {
    try {
      return strEx(path);
    } catch (Exception e) {
      return null;
    }
  }
  
  private static class Name {
    private static final Pattern END_DIGIT = Pattern.compile("^(.+)\\.(\\d+)$");
    
    public final int index;
    public final String name;
    
    public Name(String bigName) {
      if (bigName == null) {
        index = 0;
        name = null;
        return;
      }
      
      Matcher m = END_DIGIT.matcher(bigName);
      if (!m.matches()) {
        index = 0;
        name = bigName;
        return;
      }
      
      index = Integer.parseInt(m.group(2));
      name = m.group(1);
    }
    
    public String bigName() {
      if (index == 0) return name;
      return name + '.' + index;
    }
    
    @Override
    public String toString() {
      return "name = " + name + ", index = " + index;
    }
  }
  
  private String getStr(Map<String, List<Object>> map, Name name, StringBuilder prevPath) {
    List<Object> list = map.get(name.name);
    if (list == null) return null;
    
    int index = 0;
    for (Object object : list) {
      if (object instanceof String) {
        if (index == name.index) return (String)object;
        index++;
      }
    }
    
    {
      if (prevPath.length() > 0) prevPath.append("/");
      prevPath.append(name.bigName());
      throw new IndexOutOfBoundsException("No such string index for " + prevPath);
    }
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Map<String, List<Object>> getMap(Map<String, List<Object>> map, Name name,
      StringBuilder prevPath) {
    List<Object> list = map.get(name.name);
    if (list == null) return null;
    
    int index = 0;
    for (Object object : list) {
      if (object instanceof Map) {
        if (index == name.index) return (Map)object;
        index++;
      }
    }
    
    if (prevPath != null) {
      if (prevPath.length() > 0) prevPath.append("/");
      prevPath.append(name.bigName());
      throw new IndexOutOfBoundsException("No such map index for " + prevPath);
    }
    return null;
  }
  
  public List<String> list(String path) {
    List<String> ret = new ArrayList<>();
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
