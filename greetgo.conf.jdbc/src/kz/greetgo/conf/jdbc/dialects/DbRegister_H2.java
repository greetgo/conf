package kz.greetgo.conf.jdbc.dialects;

import kz.greetgo.conf.core.ConfRecord;
import kz.greetgo.conf.jdbc.FieldNames;
import kz.greetgo.conf.jdbc.NamingStyle;
import kz.greetgo.conf.jdbc.errors.NoTable;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

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
    return new RuntimeException(sqlException);
  }

  @Override
  public ConfRecord selectTableDescriptionRecord(String schema, String tableName) {
    throw new RuntimeException("Not impl yet: DbRegister_H2.selectTableDescriptionRecord");
  }

  @Override
  public void setTableComments(String schema, String tableName, FieldNames fieldNames, List<String> tableComments) {
    throw new RuntimeException("Not impl yet: DbRegister_H2.setTableComments");
  }

  @Override
  public void createTable(String schema, String tableName, FieldNames fieldNames) {
    throw new RuntimeException("Not impl yet: DbRegister_H2.createTable");
  }

  @Override
  public void createSchema(String schema) {
    throw new RuntimeException("Not impl yet: DbRegister_H2.createSchema");
  }

  @Override
  public void upsertRecord(String schema, String tableNameArg, FieldNames fieldNames, ConfRecord record) {
    throw new RuntimeException("Not impl yet: DbRegister_H2.upsertRecord");
  }
}
