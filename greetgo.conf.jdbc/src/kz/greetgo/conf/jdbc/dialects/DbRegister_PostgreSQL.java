package kz.greetgo.conf.jdbc.dialects;

import kz.greetgo.conf.core.ConfRecord;
import kz.greetgo.conf.jdbc.FieldNames;
import kz.greetgo.conf.jdbc.NamingStyle;
import kz.greetgo.conf.jdbc.errors.NoTable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class DbRegister_PostgreSQL extends DbRegister {
  protected DbRegister_PostgreSQL(NamingStyle namingStyle, DataSource dataSource) {
    super(namingStyle, dataSource);
  }

  @Override
  protected NamingStyle defaultNamingStyle() {
    return NamingStyle.LOWER_CASED;
  }

  @Override
  public RuntimeException convertSqlError(SQLException sqlException) {
    if ("42P01".equals(sqlException.getSQLState())) {
      return new NoTable(sqlException);
    }
    throw new RuntimeException(sqlException);
  }

  @Override
  protected String defaultSchema() {
    return "public";
  }

  @Override
  public ConfRecord selectTableDescriptionRecord(String schema, String tableNameArg) {
    String tableName = tableName(schema, tableNameArg);

    String sql = "SELECT obj_description('" + tableName + "'::regclass, 'pg_class')";

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        try (ResultSet rs = ps.executeQuery()) {
          return !rs.next() ? null : ConfRecord.ofComment(rs.getString(1));
        }
      }
    } catch (SQLException e) {
      throw convertSqlError(e);
    }
  }

  @Override
  public void createTable(String schema, String tableNameArg, FieldNames fieldNames) {
    String tableName   = tableName(schema, tableNameArg);
    String paramPath   = nameQuote(fieldNames.paramPath());
    String paramValue  = nameQuote(fieldNames.paramValue());
    String modifiedAt  = nameQuote(fieldNames.modifiedAt());
    String description = nameQuote(fieldNames.description());

    //noinspection MismatchedQueryAndUpdateOfStringBuilder
    StringBuilder sql = new StringBuilder();
    sql.append("create table ").append(tableName).append('(');
    sql.append(paramPath).append(" varchar(100) primary key, ");
    sql.append(paramValue).append(" text, ");
    sql.append(modifiedAt).append(" timestamp not null, ");
    sql.append(description).append(" text");
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
  public void setTableComments(String schema, String tableNameArg, FieldNames fieldNames, List<String> tableComments) {
    String tableName = tableName(schema, tableNameArg);
    String sql = "comment on table " + tableName + " is "
                   + (tableComments == null || tableComments.isEmpty()
                        ? "null"
                        : "'" + String.join("\n", tableComments).replace('\'', ' ') + "'");

    try (Connection connection = dataSource.getConnection()) {
      try (Statement statement = connection.createStatement()) {
        statement.execute(sql);
      }
    } catch (SQLException sqlException) {
      throw convertSqlError(sqlException);
    }
  }

  @Override
  public void upsertRecord(String schema, String tableNameArg, FieldNames fieldNames, ConfRecord record) {
    String tableName   = tableName(schema, tableNameArg);
    String paramPath   = nameQuote(fieldNames.paramPath());
    String paramValue  = nameQuote(fieldNames.paramValue());
    String description = nameQuote(fieldNames.description());
    String modifiedAt  = nameQuote(fieldNames.modifiedAt());

    String paramPath_Value   = record.key();
    String paramValue_Value  = record.value();
    String description_Value = record.commentValue();

    String sql = "insert into " + tableName
                   + " (" + paramPath + ", " + paramValue + ", "
                   + description + ", " + modifiedAt + ") values (?, ?, ?, current_timestamp)"
                   + " on conflict (" + paramPath + ") do update set "
                   + paramValue + " = ?, "
                   + description + " = ?, "
                   + modifiedAt + " = current_timestamp";

    System.out.println("z3Ej35CXYd :: sql = " + sql);

    try (Connection connection = dataSource.getConnection()) {

      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, paramPath_Value);
        ps.setString(2, paramValue_Value);
        ps.setString(3, description_Value);
        ps.setString(4, paramValue_Value);
        ps.setString(5, description_Value);
        ps.executeUpdate();
      }

    } catch (SQLException sqlException) {
      throw convertSqlError(sqlException);
    }
  }
}
