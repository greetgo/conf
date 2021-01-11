package kz.greetgo.conf.jdbc.dialects;

import kz.greetgo.conf.core.ConfRecord;
import kz.greetgo.conf.jdbc.FieldNames;
import kz.greetgo.conf.jdbc.JdbcType;
import kz.greetgo.conf.jdbc.NamingStyle;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public abstract class DbRegister {

  public static DbRegister create(DataSource dataSource, NamingStyle namingStyle) {

    JdbcType jdbcType = JdbcType.detect(dataSource);
    switch (jdbcType) {
      case H2:
        return new DbRegister_H2(namingStyle, dataSource);

      case PostgreSQL:
        return new DbRegister_PostgreSQL(namingStyle, dataSource);

      case MariaDb:
        return new DbRegister_MariaDb(namingStyle, dataSource);

      default:
        throw new RuntimeException("U7OFiEdbV8 :: DbRegister for " + jdbcType + " is not implemented");
    }
  }

  protected final AtomicReference<NamingStyle> namingStyle;
  protected final DataSource                   dataSource;

  protected DbRegister(NamingStyle namingStyle, DataSource dataSource) {
    this.namingStyle = new AtomicReference<>(namingStyle);
    this.dataSource  = dataSource;
  }

  protected NamingStyle namingStyle() {
    NamingStyle namingStyle = this.namingStyle.get();
    if (namingStyle != NamingStyle.DEFAULT_DIALECT) {
      return namingStyle;
    }
    {
      NamingStyle defaultNamingStyle = defaultNamingStyle();
      if (defaultNamingStyle == null || defaultNamingStyle == NamingStyle.DEFAULT_DIALECT) {
        throw new RuntimeException("X7F0eO7V26 :: Illegal defaultNamingStyle = " + defaultNamingStyle);
      }
      this.namingStyle.set(defaultNamingStyle);
      return defaultNamingStyle;
    }
  }

  protected abstract NamingStyle defaultNamingStyle();

  protected String schemaQuoted(String schema) {
    return schema == null ? defaultSchema() : nameQuote(schema);
  }

  public String tableNameQuoted(String schema, String tableName) {
    String realSchema = schemaQuoted(schema);
    return (realSchema == null ? "" : realSchema + '.') + nameQuote(tableName);
  }

  protected String defaultSchema() {
    return null;
  }

  public String nameQuote(String name) {
    return '"' + name(name) + '"';
  }

  public String name(String name) {
    switch (namingStyle()) {

      case DIRECT:
        return name;

      case LOWER_CASED:
        return name.toLowerCase();

      case UPPER_CASED:
        return name.toUpperCase();

      default:
        throw new RuntimeException("yCVDOgr0PB :: Cannot quote for " +
                                     "namingStyle = " + namingStyle() + " of name `" + name + '`');
    }
  }

  public List<ConfRecord> selectParamRecords(String schema, String tableNameArg, FieldNames fieldNames) {
    String tableName = tableNameQuoted(schema, tableNameArg);
    String paramPath = nameQuote(fieldNames.paramPath());
    String sql       = "select * from " + tableName + " order by " + paramPath;

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        try (ResultSet rs = ps.executeQuery()) {
          List<ConfRecord> ret = new ArrayList<>();
          while (rs.next()) {
            ret.add(readRecord(rs, fieldNames));
          }
          return ret;
        }
      }
    } catch (SQLException e) {
      throw convertSqlError(e);
    }
  }

  protected ConfRecord readRecord(ResultSet rs, FieldNames fieldNames) throws SQLException {
    String paramPath   = rs.getString(name(fieldNames.paramPath()));
    String paramValue  = rs.getString(name(fieldNames.paramValue()));
    String description = rs.getString(name(fieldNames.description()));
    return ConfRecord.of(paramPath, paramValue, description);
  }

  public abstract ConfRecord selectTableDescriptionRecord(String schema, String tableName);

  public abstract RuntimeException convertSqlError(SQLException sqlException);

  public abstract void upsertRecord(String schema, String tableNameArg, FieldNames fieldNames, ConfRecord record);

  public void removeRecord(String schema, String tableName, FieldNames fieldNames, String paramPath) {}

  public abstract void setTableComments(String schema, String tableName, FieldNames fieldNames, List<String> tableComments);

  public Date selectLastModifiedAt(String schema, String tableNameArg, FieldNames fieldNames) {
    String tableName  = tableNameQuoted(schema, tableNameArg);
    String modifiedAt = nameQuote(fieldNames.modifiedAt());
    String sql        = "select max(" + modifiedAt + ") from " + tableName;

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        try (ResultSet rs = ps.executeQuery()) {
          return rs.next() ? rs.getTimestamp(1) : null;
        }
      }
    } catch (SQLException e) {
      throw convertSqlError(e);
    }
  }

  public abstract void createTable(String schema, String tableName, FieldNames fieldNames);

  public abstract void createSchema(String schema);
}
