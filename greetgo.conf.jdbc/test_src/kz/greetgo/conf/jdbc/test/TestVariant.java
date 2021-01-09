package kz.greetgo.conf.jdbc.test;

import kz.greetgo.conf.jdbc.JdbcType;
import kz.greetgo.conf.jdbc.NamingStyle;

public class TestVariant {
  public final JdbcType    jdbcType;
  public final String      schema;
  public final NamingStyle namingStyle;

  private TestVariant(JdbcType jdbcType, String schema, NamingStyle namingStyle) {
    this.jdbcType    = jdbcType;
    this.schema      = schema;
    this.namingStyle = namingStyle;
  }

  public static TestVariant of(JdbcType jdbcType, String schema) {
    return new TestVariant(jdbcType, schema, NamingStyle.DEFAULT_DIALECT);
  }

  public TestVariant namingStyle(NamingStyle namingStyle) {
    return new TestVariant(jdbcType, schema, namingStyle);
  }

  @Override
  public String toString() {
    return jdbcType +
             ", schema=" + (schema == null ? "NULL" : "'" + schema + "'") +
             ", namingStyle=" + namingStyle;
  }
}
