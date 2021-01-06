package kz.greetgo.conf.core.fields;

import kz.greetgo.conf.core.util.MethodName;
import kz.greetgo.conf.core.util.StrLow;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FieldParserDefault implements FieldParser {

  public static final FieldParser instance = new FieldParserDefault();

  private final ConcurrentHashMap<Class<?>, Map<String, FieldAccess>> cache = new ConcurrentHashMap<>();

  private interface FieldId {
    String name();

    Class<?> type();
  }

  private interface FieldGetter extends FieldId {
    Object getValue(Object object);
  }

  private interface FieldSetter extends FieldId {
    void setValue(Object object, Object value);
  }

  @Override
  public Map<String, FieldAccess> parse(Class<?> aClass) {

    {
      Map<String, FieldAccess> ret = cache.get(aClass);
      if (ret != null) return ret;
    }

    {
      Map<String, FieldSetter> setters        = new HashMap<>();
      Map<String, FieldGetter> getters        = new HashMap<>();
      Set<String>              ignorableNames = new HashSet<>();

      for (Field field : aClass.getFields()) {
        if (field.getAnnotation(ConfIgnore.class) != null) {
          ignorableNames.add(field.getName());
          continue;
        }
        final String fieldName = field.getName();
        setters.put(fieldName, new FieldSetter() {
          @Override
          public String name() {
            return fieldName;
          }

          @Override
          public Class<?> type() {
            return field.getType();
          }

          @Override
          public void setValue(Object object, Object value) {
            try {
              field.set(object, value);
            } catch (IllegalAccessException e) {
              throw new RuntimeException(e);
            }
          }
        });
        getters.put(fieldName, new FieldGetter() {
          @Override
          public String name() {
            return fieldName;
          }

          @Override
          public Class<?> type() {
            return field.getType();
          }

          @Override
          public Object getValue(Object object) {
            try {
              return field.get(object);
            } catch (IllegalAccessException e) {
              throw new RuntimeException(e);
            }
          }
        });
      }

      for (Method method : aClass.getMethods()) {

        if (method.getParameterCount() == 0) {

          String name = MethodName.extractGet(method.getName());

          if (name == null) {
            continue;
          }

          if (method.getAnnotation(ConfIgnore.class) != null) {
            ignorableNames.add(name);
            continue;
          }

          getters.put(name, new FieldGetter() {
            @Override
            public Object getValue(Object object) {
              try {
                return method.invoke(object);
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
              }
            }

            @Override
            public String name() {
              return name;
            }

            @Override
            public Class<?> type() {
              return method.getReturnType();
            }
          });

          continue;
        }

        if (method.getParameterCount() == 1) {
          String methodName = method.getName();

          if (!methodName.startsWith("set")) continue;

          String name = StrLow.first(methodName.substring(3));

          if (method.getAnnotation(ConfIgnore.class) != null) {
            ignorableNames.add(name);
            continue;
          }

          setters.put(name, new FieldSetter() {
            @Override
            public void setValue(Object object, Object value) {
              try {
                method.invoke(object, value);
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
              }
            }

            @Override
            public String name() {
              return name;
            }

            @Override
            public Class<?> type() {
              return method.getParameterTypes()[0];
            }
          });
        }

      }

      {
        Map<String, FieldAccess> map = new HashMap<>();
        for (FieldGetter getter : getters.values()) {
          final String name = getter.name();
          if (ignorableNames.contains(name)) {
            continue;
          }
          FieldSetter setter = setters.get(name);
          if (setter == null) continue;
          map.put(name, new FieldAccess() {
            @Override
            public String name() {
              return name;
            }

            @Override
            public Class<?> type() {
              return getter.type();
            }

            @Override
            public void setValue(Object object, Object value) {
              setter.setValue(object, value);
            }

            @Override
            public Object getValue(Object object) {
              return getter.getValue(object);
            }

            @Override
            public String toString() {
              return FieldAccess.class.getSimpleName() + '{' + name() + '}';
            }
          });
        }
        Map<String, FieldAccess> ret = Collections.unmodifiableMap(map);
        cache.put(aClass, ret);
        return ret;
      }
    }
  }

}
