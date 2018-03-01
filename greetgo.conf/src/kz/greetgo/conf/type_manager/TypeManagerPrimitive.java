package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.ConfUtil;
import kz.greetgo.conf.hot.LineHibernate;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
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

  @Override
  public List<LineHibernate> createLineHibernateList(String topFieldName, Object defaultValue, String description) {
    return Collections.singletonList(new LineHibernate() {
      @Override
      public String fullName() {
        return topFieldName;
      }

      Object value = defaultValue;
      boolean hasStoredValue = false;

      @Override
      public void setStoredValue(String strValue, boolean commented) {
        hasStoredValue = true;
        if (!commented) value = ConfUtil.convertToType(strValue, type);
      }

      @Override
      public boolean isValueSource() {
        return true;
      }

      @Override
      public String fieldName() {
        return topFieldName;
      }

      @Override
      public Object fieldValue() {
        return value;
      }

      @Override
      public boolean hasStoredValue() {
        return hasStoredValue;
      }

      @Override
      public boolean hasContent() {
        return true;
      }

      @Override
      public String description() {
        return description;
      }

      @Override
      public String getStringDefaultValue() {
        return defaultValue == null ? null : "" + defaultValue;
      }
    });
  }
}
