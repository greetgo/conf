package kz.greetgo.conf.jdbc;

import javax.sql.DataSource;

public abstract class JdbcConfigFactory {

  protected NamingStyle namingStyle() {
    return NamingStyle.DEFAULT_DIALECT;
  }

  protected abstract DataSource dataSource();

  protected String schema() {
    return null;
  }

  protected String tablePrefix() {
    return null;
  }

  protected String tablePostfix() {
    return null;
  }

  protected String fieldName_paramPath() {
    return "param_path";
  }

  protected String fieldName_paramValue() {
    return "param_value";
  }

  protected String fieldName_modifiedAt() {
    return "modified_at";
  }

  public <T> T createConfig(Class<T> configInterface) {
    throw new RuntimeException("LAlCF9ZOmk");
  }

}
