package kz.greetgo.conf.core.fields;

import java.util.Map;

public interface FieldParser {

  Map<String, FieldAccess> parse(Class<?> aClass);

}
