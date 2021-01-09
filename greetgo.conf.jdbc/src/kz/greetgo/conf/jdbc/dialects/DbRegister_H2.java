package kz.greetgo.conf.jdbc.dialects;

import kz.greetgo.conf.core.ConfRecord;
import kz.greetgo.conf.jdbc.FieldNames;
import kz.greetgo.conf.jdbc.NamingStyle;
import kz.greetgo.conf.jdbc.errors.NoSchema;
import kz.greetgo.conf.jdbc.errors.NoTable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class DbRegister_H2 extends DbRegister {
  public DbRegister_H2(NamingStyle namingStyle, DataSource dataSource) {
    super(namingStyle, dataSource);
  }

  @Override
  protected NamingStyle defaultNamingStyle() {
    return NamingStyle.UPPER_CASED;
  }

  @Override
  public RuntimeException convertSqlError(SQLException sqlException) {
    if (sqlException.getErrorCode() == 90007) {
      return new NoTable(sqlException);
    }
    if (sqlException.getErrorCode() == 90079) {
      return new NoSchema(sqlException);
    }
    if ("42S02".equals(sqlException.getSQLState())) {
      return new NoTable(sqlException);
    }
    return new RuntimeException(sqlException);
  }

  @Override
  public ConfRecord selectTableDescriptionRecord(String schema, String tableName) {
    return null;
  }

  @Override
  public void setTableComments(String schema, String tableName, FieldNames fieldNames, List<String> tableComments) {}

  @Override
  public void createTable(String schema, String tableNameArg, FieldNames fieldNames) {

    String paramPath   = nameQuote(fieldNames.paramPath());
    String paramValue  = nameQuote(fieldNames.paramValue());
    String modifiedAt  = nameQuote(fieldNames.modifiedAt());
    String description = nameQuote(fieldNames.description());
    String tableName   = tableName(schema, tableNameArg);

    //noinspection MismatchedQueryAndUpdateOfStringBuilder
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(tableName).append('(');
    sql.append(paramPath).append(" VARCHAR(100) PRIMARY KEY, ");
    sql.append(paramValue).append(" TEXT, ");
    sql.append(modifiedAt).append(" TIMESTAMP NOT NULL, ");
    sql.append(description).append(" TEXT");
    sql.append(')');

    try (Connection connection = dataSource.getConnection()) {
      try (Statement statement = connection.createStatement()) {
        statement.execute(sql.toString());
      }
    } catch (SQLException sqlException) {
      throw convertSqlError(sqlException);
    }

  }

  @Override
  public void createSchema(String schema) {
    try (Connection connection = dataSource.getConnection()) {
      try (Statement statement = connection.createStatement()) {
        statement.execute("create schema " + schemaQuoted(schema));
      }
    } catch (SQLException e) {
      throw convertSqlError(e);
    }
  }

  @Override
  public void upsertRecord(String schema, String tableNameArg, FieldNames fieldNames, ConfRecord record) {
    throw new RuntimeException("Not impl yet: DbRegister_H2.upsertRecord");
  }
}
