package kz.greetgo.conf.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public enum JdbcType {
  H2, PostgreSQL, MariaDb;

  public static JdbcType detect(DataSource dataSource) {
    try (Connection connection = dataSource.getConnection()) {

      String databaseProductName = connection.getMetaData().getDatabaseProductName();

      if ("H2".equals(databaseProductName)) {
        return JdbcType.H2;
      }

      if ("PostgreSQL".equals(databaseProductName)) {
        return JdbcType.PostgreSQL;
      }

      if ("MariaDB".equals(databaseProductName)) {
        return JdbcType.MariaDb;
      }

      throw new RuntimeException("z011DO5ebV :: Cannot detect jdbc type:"
                                   + " databaseProductName = " + databaseProductName
                                   + ", dataSource = " + dataSource);

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
