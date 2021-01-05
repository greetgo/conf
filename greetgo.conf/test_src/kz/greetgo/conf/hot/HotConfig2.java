package kz.greetgo.conf.hot;

public interface HotConfig2 {
  @Description("Пример описания")
  @DefaultStrValue("probe def value")
  String probe();

  @Description("Пример описания\nболее длинного")
  @DefaultIntValue(456)
  int intProbe();

  @FirstReadEnv("USER")
  String userEnv();
}
