package kz.greetgo.conf.spring.cloud.hot;

import kz.greetgo.conf.hot.DefaultBoolValue;
import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Горячие конфиги ФИКС\n" //
  + "Начинается новый день\n" //
  + "И машины туда-сюда")
public interface HotConfig1 {
  @Description("Пример описания")
  @DefaultStrValue(value = "def value for strExampleValue")
  String strExampleValue();

  @Description("Пример описания intExampleValue")
  int intExampleValue();

  @Description("Пример описания intExampleValue")
  @DefaultIntValue(349)
  int intExampleValue2();

  @Description("Пример описания boolExampleValue")
  @DefaultBoolValue(true)
  boolean boolExampleValue();

  @DefaultStrValue("Привет T1001")
  String name();
}
