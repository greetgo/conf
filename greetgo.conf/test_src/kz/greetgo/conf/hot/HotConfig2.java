package kz.greetgo.conf.hot;

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
