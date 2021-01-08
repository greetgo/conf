package kz.greetgo.conf.jdbc;

import kz.greetgo.conf.jdbc.test.configs.TestConfig;
import kz.greetgo.conf.jdbc.test.db.DbManager;
import kz.greetgo.conf.jdbc.test.db.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class JdbcConfigFactoryTest {

  private final DbManager dbManager = new DbManager();

  @AfterMethod
  public void dbManager_closeAll() {
    dbManager.closeAll();
  }

  @DataProvider
  public Object[][] dbTypeProvider() {
    return new Object[][]{
      {JdbcType.H2, null},
      {JdbcType.H2, "a1"},
      {JdbcType.Postgres, null},
      {JdbcType.Postgres, "s" + RND.strEng(10)},
    };
  }

  @Test(dataProvider = "dbTypeProvider")
  public void createConfig(JdbcType jdbcType, String schema) throws SQLException {

    DataSource dataSource = dbManager.newDataSourceFor(jdbcType);

    JdbcConfigFactory configFactory = new JdbcConfigFactory() {
      @Override
      protected DataSource dataSource() {
        return dataSource;
      }
    };

    //
    //
    TestConfig config = configFactory.createConfig(TestConfig.class);
    //
    //

    assertThat(config.strParam()).isEqualTo("def value of STR");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    assertThat(sdf.format(config.dateParam())).isEqualTo("2019-01-11 23:11:10");

    String tableName = configFactory.register().tableName(schema, "TestConfig");

    System.out.println("avz1rCLm6I :: tableName = " + tableName);

    Map<String, String> params = new HashMap<>();

    try (Connection connection = dataSource.getConnection()) {

      String userName = connection.getMetaData().getUserName();
      System.out.println("n5Z1649HOG :: userName = " + userName);

      try (PreparedStatement ps = connection.prepareStatement("select * from " + tableName)) {
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            String paramPath  = rs.getString("param_path");
            String paramValue = rs.getString("param_value");
            params.put(paramPath, paramValue);
          }
        }
      }
    }

    assertThat(params).contains(entry("strParam", "def value of STR"));
    assertThat(params).contains(entry("dateParam", "2019-01-11 23:11:10"));
  }
}
