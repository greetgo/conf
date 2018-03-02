package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.ConfUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldAcceptorCreator {
  private Class<?> type;

  private FieldAcceptorCreator(Class<?> type) {this.type = type;}

  public static List<FieldAcceptor> createList(Class<?> type) {
    return new FieldAcceptorCreator(type).create();
  }

  public static Map<String, FieldAcceptor> createMap(Class<?> type) {
    return FieldAcceptorCreator
      .createList(type)
      .stream()
      .collect(Collectors.toMap(FieldAcceptor::name, Function.identity()));
  }

  interface Getter {
    Object get(Object object);
  }

  interface Setter {
    void set(Object object, Object value);
  }

  private Map<String, Getter> getterMap = new LinkedHashMap<>();
  private Map<String, Setter> setterMap = new LinkedHashMap<>();
  private Map<String, Class<?>> typeMap = new LinkedHashMap<>();

  private List<FieldAcceptor> create() {

    fillGetterMapFromFields();
    fillSetterMapFromFields();

    fillGetterMapFromGetMethods();
    fillSetterMapFromSetMethods();

    List<FieldAcceptor> ret = new ArrayList<>();

    for (String name : extractKeyList()) {
      ret.add(new FieldAcceptor() {
        @Override
        public String name() {
          return name;
        }

        final Getter getter = getterMap.get(name);
        final Setter setter = setterMap.get(name);
        final Class<?> type = typeMap.get(name);

        @Override
        public String getStrValue(Object object) {
          return ConfUtil.convertToStr(getter.get(object));
        }

        @Override
        public void setStrValue(Object object, String strValue) {
          setter.set(object, ConfUtil.convertToType(strValue, type));
        }
      });
    }


    return ret;
  }

  private List<String> extractKeyList() {
    ArrayList<String> ret = new ArrayList<>();
    for (String name : getterMap.keySet()) {
      if (setterMap.keySet().contains(name)) ret.add(name);
    }
    return ret;
  }

  private void fillGetterMapFromFields() {
    for (Field field : type.getFields()) {
      getterMap.put(field.getName(), object -> {
        try {
          return field.get(object);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private void fillSetterMapFromFields() {
    for (Field field : type.getFields()) {
      typeMap.put(field.getName(), field.getType());

      setterMap.put(field.getName(), (object, value) -> {
        try {
          field.set(object, value);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private void fillGetterMapFromGetMethods() {
    for (Method method : type.getMethods()) {

      String name = null;

      if (method.getName().length() > 3
        && method.getParameterTypes().length == 0
        && method.getName().startsWith("get")
        ) {

        name = method.getName().substring(3);
        if (name.length() > 1) {
          name = name.substring(0, 1).toLowerCase() + name.substring(1);
        } else {
          name = name.toLowerCase();
        }

      } else if (method.getName().length() > 2
        && method.getName().startsWith("is")
        && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)
        ) {
        name = method.getName().substring(2);
        if (name.length() > 1) {
          name = name.substring(0, 1).toLowerCase() + name.substring(1);
        } else {
          name = name.toLowerCase();
        }
      }

      if (name != null) getterMap.put(name, object -> {
        try {
          return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private void fillSetterMapFromSetMethods() {
    for (Method method : type.getMethods()) {

      if (method.getName().length() > 3
        && method.getParameterTypes().length == 1
        && method.getName().startsWith("set")
        ) {

        String name = method.getName().substring(3);
        if (name.length() > 1) {
          name = name.substring(0, 1).toLowerCase() + name.substring(1);
        } else {
          name = name.toLowerCase();
        }

        typeMap.put(name, method.getParameterTypes()[0]);
        setterMap.put(name, (object, value) -> {
          try {
            method.invoke(object, value);
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        });

      }

    }
  }
}
