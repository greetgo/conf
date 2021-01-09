package kz.greetgo.conf.jdbc;

import kz.greetgo.conf.jdbc.test.TestVariant;
import kz.greetgo.conf.jdbc.test.configs.TestConfig;
import kz.greetgo.conf.jdbc.test.db.DbManager;
import kz.greetgo.conf.jdbc.test.db.RND;
import kz.greetgo.conf.jdbc.test.db.access.TestDbAccess;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
  public Object[][] testVariantDataProvider() {
    List<TestVariant> variants = new ArrayList<>();
    variants.add(TestVariant.of(JdbcType.H2, null));
    variants.add(TestVariant.of(JdbcType.H2, "s" + RND.strEng(10)));
    variants.add(TestVariant.of(JdbcType.PostgreSQL, null));
    variants.add(TestVariant.of(JdbcType.PostgreSQL, "s" + RND.strEng(10)));

    return variants.stream()
                   .flatMap(this::allNamingStyles)
                   .map(x -> new Object[]{x})
                   .toArray(Object[][]::new);
  }

  private Stream<TestVariant> allNamingStyles(TestVariant testVariant) {
    return Arrays.stream(NamingStyle.values()).map(testVariant::namingStyle);
  }

  @Test(dataProvider = "testVariantDataProvider")
  public void createConfig(TestVariant tv) throws SQLException {

    DataSource dataSource = dbManager.newDataSourceFor(tv.jdbcType);

    System.out.println("AV56dSB1NR :: dataSource = " + dataSource);

    JdbcConfigFactory configFactory = new JdbcConfigFactory() {
      @Override
      protected DataSource dataSource() {
        return dataSource;
      }

      @Override
      protected String schema() {
        return tv.schema;
      }

      @Override
      protected NamingStyle namingStyle() {
        return tv.namingStyle;
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

    String tableName = configFactory.register().tableName(tv.schema, "TestConfig");

    System.out.println("avz1rCLm6I :: tableName = " + tableName);

    TestDbAccess testDbAccess = TestDbAccess.of(dataSource);

    Map<String, String> params = testDbAccess.readAsMap(tableName, "param_path", "param_value");

    assertThat(params).contains(entry("strParam", "def value of STR"));
    assertThat(params).contains(entry("dateParam", "2019-01-11 23:11:10"));
  }
}
