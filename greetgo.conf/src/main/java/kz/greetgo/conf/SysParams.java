package kz.greetgo.conf;

/**
 * Адаптер системных параметров. Считывание производится из переменных окружения
 *
 * @author pompei
 */
public class SysParams {
  
  /**
   * Получает значение системной переменной по её имени
   *
   * @param name
   *          имя системной переменной
   * @return значение системной переменной
   */
  public static String get(String name) {
    return get(name, null);
  }
  
  /**
   * Получает значение системной переменной по её имени с возможностью указания значения по
   * умолчанию
   *
   * @param name
   *          имя системной переменной
   * @param defaultValue
   *          значение по умолчанию
   * @return значение системной переменной
   */
  public static String get(String name, String defaultValue) {
    {
      String ret = System.getProperty(name);
      if (ret != null && ret.trim().length() > 0) return ret;
    }
    {
      String ret = System.getenv(name);
      if (ret != null && ret.trim().length() > 0) return ret;
    }
    return defaultValue;
  }
  
  /**
   * Получает хост для коннекта к БД оракла для возможности администрирования БД
   *
   * @return хост для коннекта к БД оракла для возможности администрирования БД
   */
  public static String oracleAdminHost() {
    return get(oracleAdminHostKey());
  }
  
  /**
   * Получает имя системной переменной, где лежит хост для коннекта к БД оракла для возможности
   * администрирования БД
   *
   * @return имя системной переменной, где лежит хост для коннекта к БД оракла для возможности
   *         администрирования БД
   */
  public static String oracleAdminHostKey() {
    return "ORACLE_ADMIN_HOST";
  }
  
  /**
   * Получает порт для коннекта к БД оракла для возможности администрирования БД
   *
   * @return порт для коннекта к БД оракла для возможности администрирования БД
   */
  public static String oracleAdminPort() {
    return get(oracleAdminPortKey());
  }
  
  /**
   * Получает имя системной переменной, где лежит порт для коннекта к БД оракла для возможности
   * администрирования БД
   *
   * @return имя системной переменной, где лежит порт для коннекта к БД оракла для возможности
   *         администрирования БД
   */
  public static String oracleAdminPortKey() {
    return "ORACLE_ADMIN_PORT";
  }
  
  /**
   * Получает SID для коннекта к БД оракла для возможности администрирования БД
   *
   * @return SID для коннекта к БД оракла для возможности администрирования БД
   */
  public static String oracleAdminSid() {
    return get(oracleAdminSidKey());
  }
  
  /**
   * Получает имя системной переменной, где лежит SID для коннекта к БД оракла для возможности
   * администрирования БД
   *
   * @return имя системной переменной, где лежит SID для коннекта к БД оракла для возможности
   *         администрирования БД
   */
  public static String oracleAdminSidKey() {
    return "ORACLE_ADMIN_SID";
  }
  
  /**
   * Получает имя пользователя для коннекта к БД оракла для возможности администрирования БД
   *
   * @return имя пользователя для коннекта к БД оракла для возможности администрирования БД
   */
  public static String oracleAdminUserid() {
    return get(oracleAdminUseridKey());
  }
  
  /**
   * Получает имя системной переменной, где лежит имя пользователя для коннекта к БД оракла для
   * возможности администрирования БД
   *
   * @return имя системной переменной, где лежит имя пользователя для коннекта к БД оракла для
   *         возможности администрирования БД
   */
  public static String oracleAdminUseridKey() {
    return "ORACLE_ADMIN_USERID";
  }
  
  /**
   * Получает пароль для коннекта к БД оракла для возможности администрирования БД
   *
   * @return пароль для коннекта к БД оракла для возможности администрирования БД
   */
  public static String oracleAdminPassword() {
    return get(oracleAdminPasswordKey());
  }
  
  /**
   * Получает имя системной переменной, где лежит пароль для коннекта к БД оракла для возможности
   * администрирования БД
   *
   * @return имя системной переменной, где лежит пароль для коннекта к БД оракла для возможности
   *         администрирования БД
   */
  public static String oracleAdminPasswordKey() {
    return "ORACLE_ADMIN_PASSWORD";
  }
  
  /**
   * Получает URL для коннекта к БД PostgreSQL для возможности администрирования БД
   *
   * @return URL для коннекта к БД PostgreSQL для возможности администрирования БД
   */
  public static String pgAdminUrl() {
    return get(pgAdminUrlKey(), "jdbc:postgresql://localhost/postgres");
  }
  
  /**
   * Получает имя системной переменной, где лежит URL для коннекта к БД PostgreSQL для возможности
   * администрирования БД
   *
   * @return имя системной переменной, где лежит URL для коннекта к БД PostgreSQL для возможности
   *         администрирования БД
   */
  public static String pgAdminUrlKey() {
    return "PG_ADMIN_URL";
  }
  
  /**
   * Получает имя пользователя для коннекта к БД PostgreSQL для возможности администрирования БД
   *
   * @return имя пользователя для коннекта к БД PostgreSQL для возможности администрирования БД
   */
  public static String pgAdminUserid() {
    return get(pgAdminUseridKey(), "postgres");
  }
  
  /**
   * Получает имя системной переменной, где лежит имя пользователя для коннекта к БД PostgreSQL для
   * возможности администрирования БД
   *
   * @return имя системной переменной, где лежит имя пользователя для коннекта к БД PostgreSQL для
   *         возможности администрирования БД
   */
  public static String pgAdminUseridKey() {
    return "PG_ADMIN_USERID";
  }
  
  /**
   * Получает пароль для коннекта к БД PostgreSQL для возможности администрирования БД
   *
   * @return пароль для коннекта к БД PostgreSQL для возможности администрирования БД
   */
  public static String pgAdminPassword() {
    return get(pgAdminPasswordKey(), "");
  }
  
  /**
   * Получает имя системной переменной, где лежит пароль для коннекта к БД PostgreSQL для
   * возможности администрирования БД
   *
   * @return имя системной переменной, где лежит пароль для коннекта к БД PostgreSQL для возможности
   *         администрирования БД
   */
  public static String pgAdminPasswordKey() {
    return "PG_ADMIN_PASSWORD";
  }
  
  public static String mysqlAdminUrlKey() {
    return "MYSQL_ADMIN_URL";
  }
  
  public static String mysqlAdminUrl() {
    return get(mysqlAdminUrlKey(), "jdbc:mysql://localhost/mysql");
  }
  
  public static String mysqlAdminUseridKey() {
    return "MYSQL_ADMIN_USERID";
  }
  
  public static String mysqlAdminUserid() {
    return get(mysqlAdminUseridKey(), "root");
  }
  
  public static String mysqlAdminPasswordKey() {
    return "MYSQL_ADMIN_PASSWORD";
  }
  
  public static String mysqlAdminPassword() {
    return get(mysqlAdminPasswordKey(), "111");
  }
  
  //  ====================MS SQL==============================
  public static String mssqlAdminHost() {
    return get(oracleAdminHostKey());
  }
  
  public static String mssqlAdminHostKey() {
    return "MSSQL_ADMIN_HOST";
  }
  
  public static String mssqlAdminPort() {
    return get(oracleAdminPortKey());
  }
  
  public static String mssqlAdminPortKey() {
    return "MSSQL_ADMIN_PORT";
  }
  
  public static String mssqlAdminUseridKey() {
    return "MSSQL_ADMIN_USERID";
  }
  
  public static String mssqlAdminUserid() {
    return get(mysqlAdminUseridKey(), "root");
  }
  
  public static String mssqlAdminPasswordKey() {
    return "MSSQL_ADMIN_PASSWORD";
  }
  
  public static String mssqlAdminPassword() {
    return get(mysqlAdminPasswordKey(), "111");
  }
  
}
