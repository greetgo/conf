package kz.greetgo.conf.jdbc.test.configs;

import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

import java.util.Date;

@Description("Test config of wow")
public interface TestConfig {
  @Description("str param of wow")
  @DefaultStrValue("def value of STR")
  String strParam();

  @Description("date param of wow")
  @DefaultStrValue("2019-01-11 23:11:10")
  Date dateParam();
}
