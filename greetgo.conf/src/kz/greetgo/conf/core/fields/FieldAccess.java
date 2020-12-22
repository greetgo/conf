package kz.greetgo.conf.core.fields;

public interface FieldAccess {

  String name();

  Class<?> type();

  void setValue(Object object, Object value);

  Object getValue(Object object);

}
