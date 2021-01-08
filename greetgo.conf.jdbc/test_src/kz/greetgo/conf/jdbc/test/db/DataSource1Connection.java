package kz.greetgo.conf.jdbc.test.db;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DataSource1Connection implements DataSource, AutoCloseable {

  private final Connection connection;

  public DataSource1Connection(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void close() throws SQLException {
    connection.close();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '{' + connection + "}@" + System.identityHashCode(this);
  }

  @Override
  public Connection getConnection() {
    return connection;
  }

  @Override
  public Connection getConnection(String username, String password) {
    throw new RuntimeException("kHtKbCHPe5 :: Not supported");
  }

  @Override
  public <T> T unwrap(Class<T> iFace) {
    throw new RuntimeException("2Vuw2OM7PL :: Not supported");
  }

  @Override
  public boolean isWrapperFor(Class<?> iFace) {
    throw new RuntimeException("4MFCjTjc8r :: Not supported");
  }

  @Override
  public PrintWriter getLogWriter() {
    throw new RuntimeException("Y8x0197bY5 :: Not supported");
  }

  @Override
  public void setLogWriter(PrintWriter out) {
    throw new RuntimeException("7H7m3A7bwt :: Not supported");
  }

  @Override
  public void setLoginTimeout(int seconds) {
    throw new RuntimeException("SyN9wt9TzJ :: Not supported");
  }

  @Override
  public int getLoginTimeout() {
    throw new RuntimeException("6IS32XBQbK :: Not supported");
  }

  @Override
  public Logger getParentLogger() {
    throw new RuntimeException("6659BfR1BX :: Not supported");
  }
}
