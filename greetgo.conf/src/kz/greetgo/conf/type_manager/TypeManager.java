package kz.greetgo.conf.type_manager;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public interface TypeManager {
  Object newDefaultValue(Object defaultValue);

  Class<?> type();

  Object extractDefaultValue(Annotation[] annotations, Function<String, String> parameterReplacer);
}
