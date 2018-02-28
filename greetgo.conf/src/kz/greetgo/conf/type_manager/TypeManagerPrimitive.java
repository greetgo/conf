package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.ConfUtil;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public class TypeManagerPrimitive implements TypeManager {
  public final Class<?> type;

  public TypeManagerPrimitive(Class<?> type) {
    if (!forMe(type)) throw new IllegalArgumentException("Wrong type " + type);
    this.type = type;
  }

  @Override
  public Object newDefaultValue(Object defaultValue) {
    return defaultValue;
  }

  @Override
  public Class<?> type() {
    return type;
  }

  public static boolean forMe(Class<?> type) {
    return type.isPrimitive() || ConfUtil.isWrapper(type) || type == String.class;
  }

  @Override
  public Object extractDefaultValue(Annotation[] annotations, Function<String, String> parameterReplacer) {
    String strValue = ConfUtil.extractStrDefaultValue(annotations, parameterReplacer);
    return ConfUtil.convertToType(strValue, type);
  }
}
