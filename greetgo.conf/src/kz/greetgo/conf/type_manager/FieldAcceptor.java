package kz.greetgo.conf.type_manager;

public interface FieldAcceptor {
  String name();

  void setStrValue(Object object, String strValue);

  String getStrValue(Object object);

  String description();
}
