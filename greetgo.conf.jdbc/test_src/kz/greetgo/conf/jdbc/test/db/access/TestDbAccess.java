package kz.greetgo.conf.jdbc.test.db.access;

import kz.greetgo.conf.jdbc.JdbcType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public abstract class TestDbAccess {

  public static TestDbAccess of(DataSource dataSource) {
    JdbcType jdbcType = JdbcType.detect(dataSource);
    switch (jdbcType) {

      case H2:
        return new TestDbAccess_H2(dataSource);

      case PostgreSQL:
        return new TestDbAccess_PostgreSQL(dataSource);

      case MariaDb:
        return new TestDbAccess_MariaDb(dataSource);

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

  public void updateParam(String tableName,
                          String fieldName, String fieldValue,
                          String name, String value) throws SQLException {

    String sql = "update " + tableName + " set " + fieldValue + " = ? where " + fieldName + " = ?";

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, value);
        ps.setString(2, name);
        int updateCount = ps.executeUpdate();
        if (updateCount != 1) throw new RuntimeException("61chGj0wl8 :: Illegal update tableName = "
                                                           + tableName + ", updateCount = " + updateCount);
      }
    }

  }

  public abstract void incrementTimestampSec(String tableName, String timestampField, int deltaSec) throws SQLException;

  public Optional<String> readParam(String tableName,
                                    String paramPath, String paramValue,
                                    String paramName) throws SQLException {

    String sql = "select " + paramValue + " from " + tableName + " where " + paramPath + " = ?";

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, paramName);
        try (ResultSet rs = ps.executeQuery()) {
          if (!rs.next()) {
            return Optional.empty();
          }
          Optional<String> ret = Optional.of(rs.getString(1));
          if (rs.next()) {
            throw new RuntimeException("oPt9gIu1qR :: Too many values of " + paramName);
          }
          return ret;
        }
      }
    }

  }

  public void insertParam(String tableName,
                          String fieldName, String fieldValue,
                          String name, String value,
                          String fieldModifiedAt) throws SQLException {

    String sql = "insert into " + tableName
                   + " (" + fieldName + ", " + fieldValue + ", " + fieldModifiedAt
                   + ") values (?, ?, current_timestamp)";

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, name);
        ps.setString(2, value);
        ps.executeUpdate();
      }
    }

  }

}
