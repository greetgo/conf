package kz.greetgo.conf.hot;

import java.util.List;

public interface ConfigLine {

  String fullName();

  List<ConfigLine> setStoredValue(String value, boolean commented);

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  boolean isStored();

  String description();

  String getNotNullDefaultStringValue();
}
