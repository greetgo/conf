package kz.greetgo.conf.jdbc.test.configs;

import kz.greetgo.conf.hot.ConfigFileName;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Test config of wow")
@ConfigFileName("TestConfig")
public interface TestConfig2 {
  @Description("str param of wow")
  @DefaultStrValue("def value of STR")
  String strParam();
}
