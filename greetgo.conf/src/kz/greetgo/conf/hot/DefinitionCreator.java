package kz.greetgo.conf.hot;

import kz.greetgo.conf.type_manager.TypeManager;
import kz.greetgo.conf.type_manager.TypeManagerCache;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class DefinitionCreator {

  private final List<ElementDefinition> elementDefinitions = new ArrayList<>();
  private String configLocation;
  private Class<?> configInterface;
  private Function<String, String> parameterReplacer;

  private HotConfigDefinitionModel result;

  private DefinitionCreator() {}

  static HotConfigDefinition createDefinition(String configLocation,
                                              Class<?> configInterface,
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
      try {
        elementDefinitions.add(createElementDefinition(method, parameterReplacer));
      } catch (HasConfigInterfaceAndMethod e) {
        throw e.setSourcePoint(configInterface, method);
      }
    }

    result = new HotConfigDefinitionModel(configLocation, extractDescription(configInterface), elementDefinitions);
  }

  private static String extractDescription(Class<?> configInterface) {
    Description a = configInterface.getAnnotation(Description.class);
    if (a == null) return null;
    return a.value();
  }

  private static ElementDefinition createElementDefinition(Method method, Function<String, String> parameterReplacer) {
    String name = method.getName();

    TypeManager typeManager = TypeManagerCache.getOrCreate(extractClass(method.getGenericReturnType()));
    Object defaultValue = typeManager.extractDefaultValue(method.getAnnotations(), parameterReplacer);
    String description = extractDescription(method);

    Integer defaultListSize = null;

    if (method.getReturnType() == List.class) {
      defaultListSize = 1;
      DefaultListSize a = method.getAnnotation(DefaultListSize.class);
      if (a != null) defaultListSize = a.value();
    }

    return ElementDefinition.create(name, typeManager, defaultValue, description, defaultListSize);
  }

  static Class<?> extractClass(Type type) {
    if (type instanceof Class) return (Class<?>) type;
    if (type instanceof ParameterizedType) {
      ParameterizedType pType = (ParameterizedType) type;
      if (pType.getRawType() == List.class) {
        return (Class<?>) pType.getActualTypeArguments()[0];
      }
      throw new RuntimeException("Cannot extract type from " + type);
    }
    throw new RuntimeException("Cannot extract type from " + type);
  }

  private static String extractDescription(Method method) {
    Description a = method.getAnnotation(Description.class);
    if (a == null) return null;
    return a.value();
  }
}
