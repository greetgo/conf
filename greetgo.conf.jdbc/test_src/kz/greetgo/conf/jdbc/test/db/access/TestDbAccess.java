package kz.greetgo.conf.jdbc.test.db.access;

import kz.greetgo.conf.jdbc.JdbcType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public abstract class TestDbAccess {

  public static TestDbAccess of(DataSource dataSource) {
    JdbcType jdbcType = JdbcType.detect(dataSource);
    switch (jdbcType) {

      case H2:
        return new TestDbAccess_H2(dataSource);

      case PostgreSQL:
        return new TestDbAccess_PostgreSQL(dataSource);

      default:
        throw new RuntimeException("6BoFvXp1d5 :: No TestDbAccess for " + jdbcType);
    }
  }

  protected final DataSource dataSource;

  protected TestDbAccess(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Map<String, String> readAsMap(String tableName, String fieldKey, String fieldValue) throws SQLException {
    Map<String, String> params = new HashMap<>();

    try (Connection connection = dataSource.getConnection()) {

      try (PreparedStatement ps = connection.prepareStatement("select * from " + tableName)) {
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            String paramPath  = rs.getString(fieldKey);
            String paramValue = rs.getString(fieldValue);
            params.put(paramPath, paramValue);
          }
        }
      }
    }
    return params;
  }
}
