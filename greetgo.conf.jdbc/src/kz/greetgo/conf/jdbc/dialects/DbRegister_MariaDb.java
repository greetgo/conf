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
public class DbRegister_MariaDb extends DbRegister {
  protected DbRegister_MariaDb(NamingStyle namingStyle, DataSource dataSource) {
    super(namingStyle, dataSource);
  }

  @Override
  protected NamingStyle defaultNamingStyle() {
    return NamingStyle.DIRECT;
  }

  @Override
  public RuntimeException convertSqlError(SQLException sqlException) {
    if ("42S02".equals(sqlException.getSQLState())) {
      return new NoTable(sqlException);
    }
    throw new RuntimeException(sqlException);
  }

  @Override
  public String nameQuote(String name) {
    return '`' + name(name) + '`';
  }

  @Override
  protected String defaultSchema() {
    return null;
  }

  @Override
  public ConfRecord selectTableDescriptionRecord(String schema, String tableNameArg) {
    String tableName = name(tableNameArg);

    String sql = "select table_comment from information_schema.TABLES " +
                   "where TABLE_NAME = ? and TABLE_SCHEMA = schema()";

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, tableName);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            String comment = rs.getString(1);
            if (comment != null && comment.trim().length() > 0) {
              return ConfRecord.ofComment(null);
            }
          }
          return null;
        }
      }
    } catch (SQLException e) {
      throw convertSqlError(e);
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
  public void createTable(String schema, String tableNameArg, FieldNames fieldNames) {
    String tableName   = tableNameQuoted(schema, tableNameArg);
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
    String tableName = tableNameQuoted(schema, tableNameArg);
    String sql = "alter table " + tableName + " comment "
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
    String tableName   = tableNameQuoted(schema, tableNameArg);
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
                   + " on duplicate key update "
                   + paramValue + " = ?, "
                   + description + " = ?, "
                   + modifiedAt + " = current_timestamp";

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
