package kz.greetgo.conf.jdbc.test.db.access;

import javax.sql.DataSource;

class TestDbAccess_H2 extends TestDbAccess {
  public TestDbAccess_H2(DataSource dataSource) {
    super(dataSource);
  }
}
