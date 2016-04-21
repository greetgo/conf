package kz.greetgo.conf.hot;

import static java.util.Collections.synchronizedList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Создаёт прокси-классы, которые предоставляют значения конфигурационных параметров из
 * конфигурационных интерфейсов, с помощью метода {@link #createConfig(Class)}
 * 
 * <p>
 * Конфигурационные файлы располагаются в папке, предоставляемой методом {@link #getBaseDir()}.
 * </p>
 * <p>
 * Имя конфигурационного файла соответствует имени конфигурационного интерфейса.
 * </p>
 * <p>
 * Расширение конфигурационного файла предоставляется методом {@link #getConfigFileExt()}
 * </p>
 * 
 * @author pompei
 */
@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
public abstract class HotConfigFactory {
  private final List<Invokator> invokators = synchronizedList(new ArrayList<Invokator>());
  private boolean resetEnabled = true;
  
  /**
   * Предоставляет имя папки, в которой будут создаваться конфигурационные файлы
   * 
   * @return имя папки, в которой будут создаваться конфигурационные файлы
   */
  protected abstract String getBaseDir();
  
  /**
   * Помечает все конфиги, чтобы они перечитались из файлов
   */
  public void reset() {
    if (!resetEnabled) return;
    
    for (Invokator i : invokators) {
      i.loaded = false;
    }
  }
  
  /**
   * Создаёт и возвращает инстанцию конфига, реализующую конфигурационный интерфейс
   * 
   * @param class1
   *          конфигурационный интерфейс
   * @return инстанцию конфига, реализующую конфигурационный интерфейс
   */
  @SuppressWarnings("unchecked")
  public <T> T createConfig(Class<T> class1) {
    return (T)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { class1 },
        getInvocator(class1));
  }
  
  private <T> Invokator getInvocator(Class<T> class1) {
    for (Invokator inv : invokators) {
      if (inv.class1.equals(class1)) return inv;
    }
    {
      Invokator invokator = new Invokator(class1);
      invokators.add(invokator);
      return invokator;
    }
  }
  
  /**
   * Создаёт и возвращает динамический модификатор параметров
   * 
   * @return динамический модификатор параметров
   */
  public HotConfigModifier createModifier() {
    return new HotConfigModifier() {
      @Override
      public void setResetEnabled(boolean resetEnabled1) {
        resetEnabled = resetEnabled1;
      }
      
      @Override
      public void set(Class<?> classs, String name, Object value) {
        for (Invokator inv : invokators) {
          if (inv.class1.equals(classs)) {
            inv.load();
            inv.data.put(name, value);
            return;
          }
        }
        
        throw new IllegalArgumentException("No hot config for class " + classs);
      }
    };
  }
  
  private class Invokator implements InvocationHandler {
    final Class<?> class1;
    boolean loaded = false;
    private final Map<String, Object> data = new ConcurrentHashMap<String, Object>();
    
    Invokator(Class<?> class1) {
      if (class1 == null) throw new IllegalArgumentException("class1 == null");
      this.class1 = class1;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (!loaded) load();
      return data.get(method.getName());
    }
    
    void load() {
      try {
        loadInner();
      } catch (Exception e) {
        if (e instanceof RuntimeException) {
          throw (RuntimeException)e;
        }
        throw new RuntimeException(e);
      }
    }
    
    void loadInner() throws Exception {
      File file = new File(getBaseDir() + File.separator + class1.getSimpleName()
          + getConfigFileExt());
      Map<String, String> fileData = new HashMap<String, String>();
      
      if (file.exists()) {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.toURI().toURL()
            .openStream(), "UTF-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
          parseAndAddLine(fileData, line);
        }
        br.close();
      }
      
      Map<String, Object> readData = new HashMap<String, Object>();
      final Set<String> namesToAdd = new HashSet<String>();
      
      for (Method method : class1.getDeclaredMethods()) {
        String name = method.getName();
        if (!fileData.keySet().contains(name)) {
          namesToAdd.add(name);
        }
        
        Class<?> returnType = method.getReturnType();
        
        if (String.class.isAssignableFrom(returnType)) {
          if (fileData.containsKey(name)) {
            readData.put(name, fileData.get(name));
          } else {
            readData.put(name, readString(method));
          }
          continue;
        }
        
        if (Enum.class.isAssignableFrom(returnType)) {
          if (fileData.containsKey(name)) {
            readData.put(name, convertToEnum(fileData.get(name), returnType));
          } else {
            readData.put(name, readEnum(method, returnType));
          }
          continue;
        }
        
        if (Integer.class.isAssignableFrom(returnType) || Integer.TYPE.isAssignableFrom(returnType)) {
          String str = fileData.get(name);
          if (str == null) {
            readData.put(name, readInt(method, returnType));
          } else {
            try {
              readData.put(name, Integer.valueOf(str));
            } catch (java.lang.NumberFormatException e) {
              readData.put(name, 0);
            }
          }
          continue;
        }
        
        if (Boolean.class.isAssignableFrom(returnType) || Boolean.TYPE.isAssignableFrom(returnType)) {
          String str = fileData.get(name);
          if (str == null) {
            readData.put(name, readBool(method, returnType));
          } else {
            readData.put(name, strToBool(str));
            
          }
          continue;
        }
        
        throw new IllegalArgumentException("Unable to read value of type " + returnType);
      }
      
      if (namesToAdd.size() > 0) {
        new File(getBaseDir()).mkdirs();
        FileOutputStream fout = new FileOutputStream(file, true);
        OutputStreamWriter outWriter = new OutputStreamWriter(fout, "UTF-8");
        BufferedWriter bufWriter = new BufferedWriter(outWriter, 2048);
        PrintWriter pr = new PrintWriter(bufWriter, false);
        
        pr.println();
        pr.println("# ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        pr.println("# Updated at " + sdf.format(new Date()));
        pr.println("# ");
        
        {
          Description ann = class1.getAnnotation(Description.class);
          if (ann != null) {
            String value = ann.value();
            if (value != null) {
              String[] split = value.split("\n");
              for (String line : split) {
                pr.println("# " + line);
              }
              pr.println("# ");
            }
          }
        }
        
        for (Method method : class1.getDeclaredMethods()) {
          String name = method.getName();
          if (!namesToAdd.contains(name)) continue;
          printParameterIni(pr, method);
        }
        
        pr.flush();
        pr.close();
      }
      
      {
        data.clear();
        data.putAll(readData);
        loaded = true;
      }
    }
    
    void printParameterIni(PrintWriter pr, Method method) throws Exception {
      pr.println();
      pr.println();
      
      Description descr = method.getAnnotation(Description.class);
      if (descr != null) {
        pr.println("##");
        for (String docStr : descr.value().split("\\n")) {
          pr.println("## " + docStr);
        }
        pr.println("##");
      }
      
      String name = method.getName();
      
      Class<?> returnType = method.getReturnType();
      
      if (String.class.isAssignableFrom(returnType)) {
        String defValue = null;
        {
          DefaultStrValue ann = method.getAnnotation(DefaultStrValue.class);
          if (ann != null) {
            defValue = ann.value();
          }
        }
        {
          DefaultIntValue ann = method.getAnnotation(DefaultIntValue.class);
          if (ann != null) {
            defValue = "" + ann.value();
          }
        }
        
        if (defValue == null) {
          pr.println(name);
        } else {
          pr.println(name + "=" + defValue);
        }
        return;
      }
      
      if (Integer.class.isAssignableFrom(returnType) || Integer.TYPE.isAssignableFrom(returnType)) {
        int defValue = 0;
        {
          DefaultIntValue ann = method.getAnnotation(DefaultIntValue.class);
          if (ann != null) {
            defValue = ann.value();
          }
        }
        
        pr.println(name + "=" + defValue);
        return;
      }
      
      if (Boolean.class.isAssignableFrom(returnType) || Boolean.TYPE.isAssignableFrom(returnType)) {
        boolean defValue = false;
        {
          DefaultBoolValue ann = method.getAnnotation(DefaultBoolValue.class);
          if (ann != null) {
            defValue = ann.value();
          }
        }
        
        pr.println(name + "=" + defValue);
        return;
      }
      
      if (Enum.class.isAssignableFrom(returnType)) {
        Object enumValue = readEnum(method, returnType);
        if (enumValue == null) {
          pr.println(name);
        }
        
        Method getEnumName = returnType.getMethod("name");
        
        for (Object value : returnType.getEnumConstants()) {
          if (!value.equals(enumValue)) {
            pr.print("#");
          }
          pr.println(name + "=" + getEnumName.invoke(value));
        }
        return;
      }
      
      throw new IllegalArgumentException("Cannot make default value for type " + returnType);
    }
    
    Object convertToEnum(String strValue, Class<?> enumType) throws Exception {
      try {
        Method valueOf = enumType.getMethod("valueOf", String.class);
        return valueOf.invoke(null, strValue);
      } catch (InvocationTargetException e) {
        return null;
      }
    }
    
    String readString(Method method) {
      DefaultStrValue value = method.getAnnotation(DefaultStrValue.class);
      if (value == null) return null;
      return value.value();
    }
    
    private int readInt(Method method, Class<?> returnType) {
      DefaultIntValue value = method.getAnnotation(DefaultIntValue.class);
      if (value == null) return 0;
      return value.value();
    }
    
    private boolean readBool(Method method, Class<?> returnType) {
      DefaultBoolValue value = method.getAnnotation(DefaultBoolValue.class);
      if (value == null) return false;
      return value.value();
    }
    
    Object readEnum(Method method, Class<?> enumClass) throws Exception {
      try {
        Method meth = enumClass.getDeclaredMethod("getDefaultValue");
        return meth.invoke(null);
      } catch (NoSuchMethodException e) {
        return enumClass.getEnumConstants()[0];
      }
    }
    
    void parseAndAddLine(Map<String, String> fileData, String line) {
      if (line == null) return;
      line = line.trim();
      if (line.startsWith("#")) return;
      int eqIndex = line.indexOf('=');
      if (eqIndex < 0) {
        fileData.put(line, null);
        return;
      }
      fileData.put(line.substring(0, eqIndex).trim(), line.substring(eqIndex + 1).trim());
    }
  }
  
  public boolean strToBool(String str) {
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
    
    return false;
  }
  
  protected String getConfigFileExt() {
    return ".hotconfig";
  }
}
