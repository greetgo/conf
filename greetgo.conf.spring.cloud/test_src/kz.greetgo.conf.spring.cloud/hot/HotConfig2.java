package kz.greetgo.conf.spring.cloud.hot;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;
import kz.greetgo.conf.hot.FirstReadEnv;

public interface HotConfig2 {
  @Description("Пример описания")
  @DefaultStrValue("asd def value")
  String asd();

  @Description("Пример описания\nболее длинного")
  @DefaultIntValue(456)
  int intAsd();

  @FirstReadEnv("USER")
  String userEnv();
}
