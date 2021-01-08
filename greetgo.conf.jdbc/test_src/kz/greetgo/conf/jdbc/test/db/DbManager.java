package kz.greetgo.conf.jdbc.test.db;

import kz.greetgo.conf.jdbc.JdbcType;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DbManager {
  private final Path rootDir = Paths.get("build")
                                    .resolve(getClass().getSimpleName())
                                    .resolve("dbs");

  private final ConcurrentLinkedQueue<DataSource1Connection> dataSources = new ConcurrentLinkedQueue<>();

  public DataSource newDataSourceFor(JdbcType jdbcType) {
    try {
      switch (jdbcType) {
        case H2:
          return newDataSource_H2();

        case Postgres:
          return newDataSource_Postgres();

        default:
          throw new RuntimeException("G2lJPm4D8S :: Unknown JdbcType " + jdbcType);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
  private DataSource newDataSource_Postgres() throws Exception {
    Class.forName("org.postgresql.Driver");
    String rnd      = RND.strEng(10).toLowerCase();
    String user     = "user_" + rnd;
    String password = RND.strEng(15).toLowerCase();
    String db       = "db_" + rnd;

    try (Connection admin = DriverManager.getConnection("jdbc:postgresql://localhost:25432/postgres", "postgres", "111")) {

      try (Statement statement = admin.createStatement()) {
        statement.execute("create user " + user + " with encrypted password '" + password + "'");
        statement.execute("create database " + db + " with owner " + user);
        statement.execute("grant all privileges on database " + db + " to " + user);
      }

    }

    return toDataSource(DriverManager.getConnection("jdbc:postgresql://localhost:25432/" + db, user, password));
  }

  private DataSource newDataSource_H2() throws Exception {
    Class.forName("org.h2.Driver");
    String dbFilePath = rootDir.resolve("H2").resolve(RND.strEng(15)).toAbsolutePath().toString();
    new File(dbFilePath).getParentFile().mkdirs();
    return toDataSource(DriverManager.getConnection("jdbc:h2:" + dbFilePath, "sa", ""));
  }

  private DataSource toDataSource(Connection connection) {
    DataSource1Connection dataSource = new DataSource1Connection(connection);
    dataSources.add(dataSource);
    return dataSource;
  }

  public void closeAll() {

    while (true) {
      DataSource1Connection dataSource = dataSources.poll();
      if (dataSource == null) return;
      try {
        dataSource.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

  }
}
