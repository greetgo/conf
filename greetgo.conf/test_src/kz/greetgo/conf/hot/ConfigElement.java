package kz.greetgo.conf.hot;

@SuppressWarnings("unused")
public interface ConfigElement {

  @DefaultIntValue(20019)
  int intField();

  @DefaultStrValue("By one")
  String strField();

}
