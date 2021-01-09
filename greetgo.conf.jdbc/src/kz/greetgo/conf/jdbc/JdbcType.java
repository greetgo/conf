package kz.greetgo.conf.jdbc;

import kz.greetgo.conf.jdbc.dialects.DbRegister_H2;
import kz.greetgo.conf.jdbc.dialects.DbRegister_PostgreSQL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public enum JdbcType {
  H2, PostgreSQL;

  public static JdbcType detect(DataSource dataSource) {
    try (Connection connection = dataSource.getConnection()) {

      String databaseProductName = connection.getMetaData().getDatabaseProductName();

      if ("H2".equals(databaseProductName)) {
        return JdbcType.H2;
      }

      if ("PostgreSQL".equals(databaseProductName)) {
        return JdbcType.PostgreSQL;
      }

      throw new RuntimeException("z011DO5ebV :: Cannot detect jdbc type:"
                                   + " databaseProductName = " + databaseProductName
                                   + ", dataSource = " + dataSource);

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
