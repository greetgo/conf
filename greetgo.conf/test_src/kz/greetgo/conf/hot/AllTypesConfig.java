package kz.greetgo.conf.hot;

@ForcibleInit
@SuppressWarnings("unused")
@Description("Config description CHE")
public interface AllTypesConfig {

  @Description("str value description")
  @DefaultStrValue("def-str-val WOW")
  String strValue();

  @Description("int value description")
  @DefaultIntValue(123)
  int intValue();

  @Description("long value description")
  @DefaultLongValue(765876)
  long longValue();

  @Description("bool value 1 description")
  @DefaultBoolValue(true)
  boolean boolValue1();

  @Description("bool value 2 description")
  @DefaultBoolValue(false)
  boolean boolValue2();

  @Description("double value description")
  @DefaultDoubleValue(123.072)
  double doubleValue();

}
