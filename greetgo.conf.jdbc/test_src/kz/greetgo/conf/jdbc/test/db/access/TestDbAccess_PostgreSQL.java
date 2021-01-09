package kz.greetgo.conf.jdbc.test.db.access;

import javax.sql.DataSource;

class TestDbAccess_PostgreSQL extends TestDbAccess {
  public TestDbAccess_PostgreSQL(DataSource dataSource) {
    super(dataSource);
  }
}
