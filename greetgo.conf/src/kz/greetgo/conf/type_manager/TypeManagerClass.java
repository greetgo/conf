package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.hot.LineHibernate;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;

public class TypeManagerClass implements TypeManager {
  public final Class<?> type;

  public TypeManagerClass(Class<?> type) {
    this.type = type;
  }

  @Override
  public Object newDefaultValue(Object defaultValue) {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Class<?> type() {
    return type;
  }

  @Override
  public Object extractDefaultValue(Annotation[] annotations, Function<String, String> parameterReplacer) {
    //simple return null, because default value would be get by method #newDefaultValue(Object)
    return null;
  }

  @Override
  public List<LineHibernate> createLineHibernateList(String topFieldName, Object defaultValue) {
    throw new RuntimeException("Just not working");
  }
}
