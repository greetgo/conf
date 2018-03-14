package kz.greetgo.conf.sys_params;

/**
 * The adapter of system parameters. Reading is made from environment variables
 *
 * @author pompei
 */
public class SysParams {

  /**
   * Receives the value of system variable under its name
   *
   * @param name name of system variable
   * @return value of system variable
   */
  public static String get(String name) {
    return get(name, null);
  }

  /**
   * Receives the value of system variable under its name with the ability to specify the default value
   *
   * @param name         name of system variable
   * @param defaultValue default value
   * @return value of system variable
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
   * receives the host for connection to DB Oracle to allow administration of DB
   *
   * @return the host for connection to DB Oracle to allow administration of DB
   */
  public static String oracleAdminHost() {
    return get(oracleAdminHostKey());
  }

  /**
   * Receives the name of system variable, where the host is for connection to DB Oracle to allow
   * administration of DB
   *
   * @return the name of system variable, where the host is for connection to DB Oracle to allow
   * administration of DB
   */
  public static String oracleAdminHostKey() {
    return "ORACLE_ADMIN_HOST";
  }

  /**
   * Receives the port for connection to DB Oracle to allow administration of DB
   *
   * @return the port for connection to DB Oracle to allow administration of DB
   */
  public static String oracleAdminPort() {
    return get(oracleAdminPortKey());
  }

  /**
   * Receives the name of system variable, where the port is for connection to DB Oracle to allow
   * administration of DB
   *
   * @return the name of system variable, where the port is for connection to DB Oracle to allow
   * administration of DB
   */
  public static String oracleAdminPortKey() {
    return "ORACLE_ADMIN_PORT";
  }

  /**
   * Receives SID for connection to DB Oracle to allow administration of DB
   *
   * @return SID for connection to DB Oracle to allow administration of DB
   */
  public static String oracleAdminSid() {
    return get(oracleAdminSidKey());
  }

  /**
   * Receives the name of system variable, where SID is for connection to DB Oracle to allow
   * administration of DB
   *
   * @return the name of system variable, where SID is for connection to DB Oracle to allow
   * administration of DB
   */
  public static String oracleAdminSidKey() {
    return "ORACLE_ADMIN_SID";
  }

  /**
   * Receives the user name for connection to DB Oracle to allow administration of DB
   *
   * @return the user name for connection to DB Oracle to allow administration of DB
   */
  public static String oracleAdminUserid() {
    return get(oracleAdminUseridKey());
  }

  /**
   * Receives the name of system variable, where user name is for connection to DB Oracle
   * to allow administration of DB
   *
   * @return the name of system variable, where user name is for connection to DB Oracle
   * to allow administration of DB
   */
  public static String oracleAdminUseridKey() {
    return "ORACLE_ADMIN_USERID";
  }

  /**
   * Receives the password for connection to DB Oracle to allow administration of DB
   *
   * @return the password for connection to DB Oracle to allow administration of DB
   */
  public static String oracleAdminPassword() {
    return get(oracleAdminPasswordKey());
  }

  /**
   * Receives the name of system variable, where password is for connection to DB Oracle to allow
   * administration of DB
   *
   * @return the name of system variable, where password is for connection to DB Oracle to allow
   * administration of DB
   */
  public static String oracleAdminPasswordKey() {
    return "ORACLE_ADMIN_PASSWORD";
  }

  /**
   * Receives URL for connection to DB PostgreSQL to allow administration of DB
   *
   * @return URL for connection to DB PostgreSQL to allow administration of DB
   */
  public static String pgAdminUrl() {
    return get(pgAdminUrlKey(), "jdbc:postgresql://localhost/postgres");
  }

  /**
   * Receives the name of system variable, where URL is for connection to DB PostgreSQL to allow
   * administration of DB
   *
   * @return the name of system variable, where URL is for connection to DB PostgreSQL to allow
   * administration of DB
   */
  public static String pgAdminUrlKey() {
    return "PG_ADMIN_URL";
  }

  /**
   * Receives the user name for connection to DB PostgreSQL to allow administration of DB
   *
   * @return the user name for connection to DB PostgreSQL to allow administration of DB
   */
  public static String pgAdminUserid() {
    return get(pgAdminUseridKey(), "postgres");
  }

  /**
   * Receives the name of system variable, where user name is for connection to DB PostgreSQL
   * to allow administration of DB
   *
   * @return the name of system variable, where user name is for connection to DB PostgreSQL
   * to allow administration of DB
   */
  public static String pgAdminUseridKey() {
    return "PG_ADMIN_USERID";
  }

  /**
   * Receives the password for connection to DB PostgreSQL to allow administration of DB
   *
   * @return the password for connection to DB PostgreSQL to allow administration of DB
   */
  public static String pgAdminPassword() {
    return get(pgAdminPasswordKey(), "");
  }

  /**
   * Receives the name of system variable, where password is for connection to DB PostgreSQL
   * to allow administration of DB
   *
   * @return the name of system variable, where password is for connection to DB PostgreSQL to allow
   * administration of DB
   */
  public static String pgAdminPasswordKey() {
    return "PG_ADMIN_PASSWORD";
  }

  /**
   * Receives the name of system variable, where URL is for connection to DB MySQL
   * to allow administration of DB
   *
   * @return the name of system variable, where password is for connection to DB MySQL to allow
   * administration of DB
   */

  public static String mysqlAdminUrlKey() {
    return "MYSQL_ADMIN_URL";
  }

  /**
   * Receives URL for connection to DB MySQL to allow administration of DB
   *
   * @return URL for connection to DB MySQL to allow administration of DB
   */
  public static String mysqlAdminUrl() {
    return get(mysqlAdminUrlKey(), "jdbc:mysql://localhost/mysql");
  }

  /**
   * Receives the name of system variable, where user name is for connection to DB MySQL
   * to allow administration of DB
   *
   * @return the name of system variable, where user name is for connection to DB MySQL
   * to allow administration of DB
   */
  public static String mysqlAdminUseridKey() {
    return "MYSQL_ADMIN_USERID";
  }

  /**
   * Receives the user name for connection to DB MySQL to allow administration of DB
   *
   * @return the user name for connection to DB MySQL to allow administration of DB
   */

  public static String mysqlAdminUserid() {
    return get(mysqlAdminUseridKey(), "root");
  }

  /**
   * Receives the name of system variable, where password is for connection to DB MySQL
   * to allow administration of DB
   *
   * @return the name of system variable, where password is for connection to DB MySQL to allow
   * administration of DB
   */

  public static String mysqlAdminPasswordKey() {
    return "MYSQL_ADMIN_PASSWORD";
  }

  /**
   * Receives the password for connection to DB MySQL to allow administration of DB
   *
   * @return the password for connection to DB MySQL to allow administration of DB
   */

  public static String mysqlAdminPassword() {
    return get(mysqlAdminPasswordKey(), "111");
  }

  /**
   * receives the host for connection to DB MS SQL Server to allow administration of DB
   *
   * @return the host for connection to DB MS SQL Server to allow administration of DB
   */

  public static String mssqlAdminHost() {
    return get(mssqlAdminHostKey());
  }

  /**
   * Receives the name of system variable, where the host is for connection to DB MS SQL Server to allow
   * administration of DB
   *
   * @return the name of system variable, where the host is for connection to DB MS SQL Server to allow
   * administration of DB
   */

  public static String mssqlAdminHostKey() {
    return "MSSQL_ADMIN_HOST";
  }

  /**
   * Receives the port for connection to DB MS SQL Server to allow administration of DB
   *
   * @return the port for connection to DB MS SQL Server to allow administration of DB
   */

  public static String mssqlAdminPort() {
    return get(mssqlAdminPortKey());
  }

  /**
   * Receives the name of system variable, where the port is for connection to DB MS SQL Server to allow
   * administration of DB
   *
   * @return the name of system variable, where the port is for connection to DB MS SQL Server to allow
   * administration of DB
   */
  public static String mssqlAdminPortKey() {
    return "MSSQL_ADMIN_PORT";
  }

  /**
   * Receives the name of system variable, where user name is for connection to DB MS SQL Server
   * to allow administration of DB
   *
   * @return the name of system variable, where user name is for connection to DB MS SQL Server
   * to allow administration of DB
   */
  public static String mssqlAdminUseridKey() {
    return "MSSQL_ADMIN_USERID";
  }

  /**
   * Receives the user name for connection to DB MS SQL Server to allow administration of DB
   *
   * @return the user name for connection to DB MS SQL Server to allow administration of DB
   */

  public static String mssqlAdminUserid() {
    return get(mssqlAdminUseridKey(), "root");
  }

  /**
   * Receives the name of system variable, where password is for connection to DB MS SQL Server
   * to allow administration of DB
   *
   * @return the name of system variable, where password is for connection to DB MS SQL Server to allow
   * administration of DB
   */
  public static String mssqlAdminPasswordKey() {
    return "MSSQL_ADMIN_PASSWORD";
  }

  /**
   * Receives the password for connection to DB MS SQL Server to allow administration of DB
   *
   * @return the password for connection to DB MS SQL Server to allow administration of DB
   */

  public static String mssqlAdminPassword() {
    return get(mssqlAdminPasswordKey(), "111");
  }

  /**
   * Receives the name of system variable, where the string of access to zookeeper is located
   *
   * @return the name of system variable, where the string of access to zookeeper is located
   */
  public static String zookeeperConnectStrKey() {
    return "ZOOKEEPER_CONNECT_STR";
  }

  /**
   * Receives the string of access to zookeeper
   *
   * @return the string of access to zookeeper
   */
  public static String zookeeperConnectStr() {
    return get(zookeeperConnectStrKey(), "localhost:2181");
  }
}