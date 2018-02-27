package kz.greetgo.conf.hot;

import kz.greetgo.conf.ConfUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class DefinitionCreator<T> {

  private final List<ElementDefinition> elementDefinitions = new ArrayList<>();
  private String configLocation;
  private Class<T> configInterface;
  private Function<String, String> parameterReplacer;

  private HotConfigDefinitionModel result;

  static <T> HotConfigDefinition createDefinition(String configLocation,
                                                  Class<T> configInterface,
                                                  Function<String, String> parameterReplacer) {

    DefinitionCreator definitionCreator = new DefinitionCreator();
    definitionCreator.configLocation = configLocation;
    definitionCreator.configInterface = configInterface;
    definitionCreator.parameterReplacer = parameterReplacer;

    definitionCreator.create();

    return definitionCreator.result;
  }

  private void create() {

    for (Method method : configInterface.getMethods()) {
      elementDefinitions.add(createHotElementDefinition(method, parameterReplacer));
    }

    result = new HotConfigDefinitionModel(configLocation, extractDescription(configInterface), elementDefinitions);

  }

  private <T> String extractDescription(Class<T> configInterface) {
    Description a = configInterface.getAnnotation(Description.class);
    if (a == null) return null;
    return a.value();
  }

  private ElementDefinition createHotElementDefinition(Method method,
                                                       Function<String, String> parameterReplacer) {
    String name = method.getName();
    Class<?> type = method.getReturnType();
    Object defaultValue = extractDefaultValue(method, parameterReplacer);
    String description = extractDescription(method);
    return new ElementDefinition(name, type, defaultValue, description);
  }

  private String extractDescription(Method method) {
    Description a = method.getAnnotation(Description.class);
    if (a == null) return null;
    return a.value();
  }

  private Object extractDefaultValue(Method method, Function<String, String> parameterReplacer) {
    Class<?> type = method.getReturnType();

    if (type == String.class) {
      {
        DefaultStrValue a = method.getAnnotation(DefaultStrValue.class);
        if (a != null) return parameterReplacer.apply(a.value());
      }

      {
        DefaultIntValue a = method.getAnnotation(DefaultIntValue.class);
        if (a != null) return "" + a.value();
      }
      {
        DefaultLongValue a = method.getAnnotation(DefaultLongValue.class);
        if (a != null) return "" + a.value();
      }
      {
        DefaultBoolValue a = method.getAnnotation(DefaultBoolValue.class);
        if (a != null) return "" + a.value();
      }

      return null;
    }

    if (type == int.class || type == Integer.class) {
      {
        DefaultIntValue a = method.getAnnotation(DefaultIntValue.class);
        if (a != null) return a.value();
      }
      {
        DefaultLongValue a = method.getAnnotation(DefaultLongValue.class);
        if (a != null) return (int) a.value();
      }
      {
        DefaultStrValue a = method.getAnnotation(DefaultStrValue.class);
        if (a != null && a.value().trim().length() > 0) return Integer.parseInt(a.value().trim());
      }
      {
        DefaultBoolValue a = method.getAnnotation(DefaultBoolValue.class);
        if (a != null) return a.value() ? 1 : 0;
      }
      return type == int.class ? 0 : null;
    }

    if (type == long.class || type == Long.class) {
      {
        DefaultIntValue a = method.getAnnotation(DefaultIntValue.class);
        if (a != null) return (long) a.value();
      }
      {
        DefaultLongValue a = method.getAnnotation(DefaultLongValue.class);
        if (a != null) return a.value();
      }
      {
        DefaultStrValue a = method.getAnnotation(DefaultStrValue.class);
        if (a != null && a.value().trim().length() > 0) return Long.parseLong(a.value().trim());
      }
      {
        DefaultBoolValue a = method.getAnnotation(DefaultBoolValue.class);
        if (a != null) return a.value() ? 1L : 0L;
      }
      return type == long.class ? 0L : null;
    }

    if (type == boolean.class || type == Boolean.class) {
      {
        DefaultBoolValue a = method.getAnnotation(DefaultBoolValue.class);
        if (a != null) return a.value();
      }
      {
        DefaultStrValue a = method.getAnnotation(DefaultStrValue.class);
        if (a != null) return ConfUtil.strToBool(a.value());
      }

      return type == boolean.class ? false : null;
    }

    throw new RuntimeException("Cannot generate default value for type " + type);
  }
}
