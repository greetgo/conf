package kz.greetgo.conf.env;

import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа к postgres БД AUX1 - это вспомогательная БД для работы со списками.\n" +
               "К этой БД данные поступают из кафки и заполняют соответствующие денормализованные таблицы,\n" +
               "а потом система из этих таблиц строит списки по запросу клиента")
public interface TestConfig {

  @Description("Имя БД постреса")
  @EnvName("MYBPM_AUX1_DB_NAME")
  String dbName();

  @Description("Хост постреса")
  @EnvName("MYBPM_AUX1_HOST")
  String host();

  @Description("Порт постгреса (число от 1025 до 65536 ; обчно 5432)")
  @EnvName("MYBPM_AUX1_PORT")
  int port();

  @Description("Имя пользователя в БД для доступа к нему")
  @EnvName("MYBPM_AUX1_USER_NAME")
  String username();

  @Description("Пароль доступа к БД")
  @EnvName("MYBPM_AUX1_PASSWORD")
  String password();

  default String url() {
    return "jdbc:postgresql://" + host() + ":" + port() + "/" + dbName();
  }

}
