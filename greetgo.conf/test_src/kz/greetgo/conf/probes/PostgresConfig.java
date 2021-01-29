package kz.greetgo.conf.probes;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Доступ к исходным данным")
public interface PostgresConfig {
  @Description("Для нас создают специальную схему - её надо запрашивать у СБ")
  String schema();

  @DefaultStrValue("some-host")
  String host();

  @DefaultIntValue(5432)
  int port();

  @DefaultStrValue("source")
  String username();

  @DefaultStrValue("[migration.source-pg]")
  String password();
}
