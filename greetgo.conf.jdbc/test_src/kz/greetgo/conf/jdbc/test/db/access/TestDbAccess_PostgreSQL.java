package kz.greetgo.conf.jdbc.test.db.access;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
class TestDbAccess_PostgreSQL extends TestDbAccess {
  public TestDbAccess_PostgreSQL(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public void incrementTimestampSec(String tableName, String timestampField, int deltaSec) throws SQLException {

    String sql = "update " + tableName + " set " + timestampField
                   + " = " + timestampField + " + '" + deltaSec + " seconds'";

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        int updateCount = ps.executeUpdate();
        if (updateCount == 0) {
          throw new RuntimeException("fEPHOipQWu :: No updates on SQL: " + sql);
        }
      }
    }

  }
}
