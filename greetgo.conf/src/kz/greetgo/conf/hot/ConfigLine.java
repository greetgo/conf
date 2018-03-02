package kz.greetgo.conf.hot;

public interface ConfigLine {
  String fullName();

  void setStoredValue(String value, boolean commented);

  boolean isStored();

  String description();

  String getNotNullDefaultStringValue();
}
