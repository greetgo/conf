package kz.greetgo.conf.jdbc;

import kz.greetgo.conf.jdbc.dialects.DbRegister;
import kz.greetgo.conf.jdbc.test.TestVariant;
import kz.greetgo.conf.jdbc.test.configs.TestConfig;
import kz.greetgo.conf.jdbc.test.configs.TestConfig2;
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
import java.util.concurrent.atomic.AtomicLong;
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
    variants.add(TestVariant.of(JdbcType.MariaDb, null));

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

    String tableName = configFactory.register().tableNameQuoted(tv.schema, "TestConfig");

    System.out.println("avz1rCLm6I :: tableName = " + tableName);

    TestDbAccess testDbAccess = TestDbAccess.of(dataSource);

    Map<String, String> params = testDbAccess.readAsMap(tableName, "param_path", "param_value");

    assertThat(params).contains(entry("strParam", "def value of STR"));
    assertThat(params).contains(entry("dateParam", "2019-01-11 23:11:10"));
  }

  @Test(dataProvider = "testVariantDataProvider")
  public void createConfig_anotherNames_checkConfigReread(TestVariant tv) throws Exception {

    DataSource dataSource = dbManager.newDataSourceFor(tv.jdbcType);

    System.out.println("AV56dSB1NR :: dataSource = " + dataSource);

    FieldNames fn = new FieldNames() {
      final String paramPath = "p" + RND.strEng(10);

      @Override
      public String paramPath() {
        return paramPath;
      }

      final String paramValue = "v" + RND.strEng(10);

      @Override
      public String paramValue() {
        return paramValue;
      }

      final String description = "d" + RND.strEng(10);

      @Override
      public String description() {
        return description;
      }

      final String modifiedAt = "m" + RND.strEng(10);

      @Override
      public String modifiedAt() {
        return modifiedAt;
      }
    };

    AtomicLong time = new AtomicLong(1000);

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

      @Override
      public FieldNames fieldNames() {
        return fn;
      }

      @Override
      protected long autoResetTimeout() {
        return 3000;
      }

      @Override
      protected long currentTimeMillis() {
        return time.get();
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

    DbRegister register = configFactory.register();

    String tableName = register.tableNameQuoted(tv.schema, "TestConfig");

    System.out.println("avz1rCLm6I :: tableName = " + tableName);

    TestDbAccess testDbAccess = TestDbAccess.of(dataSource);

    Map<String, String> params = testDbAccess.readAsMap(tableName, fn.paramPath(), fn.paramValue());

    assertThat(params).contains(entry("strParam", "def value of STR"));
    assertThat(params).contains(entry("dateParam", "2019-01-11 23:11:10"));

    String newStrParamValue = RND.strEng(30);

    String paramPath  = register.nameQuote(fn.paramPath());
    String paramValue = register.nameQuote(fn.paramValue());
    String modifiedAt = register.nameQuote(fn.modifiedAt());

    testDbAccess.updateParam(tableName, paramPath, paramValue, "strParam", newStrParamValue);
    testDbAccess.incrementTimestampSec(tableName, modifiedAt, 3);

    assertThat(config.strParam()).isEqualTo("def value of STR");

    time.addAndGet(10_000);

    assertThat(config.strParam()).isEqualTo(newStrParamValue);
  }

  @Test(dataProvider = "testVariantDataProvider")
  public void createConfig_removeParam(TestVariant tv) throws Exception {

    DataSource dataSource = dbManager.newDataSourceFor(tv.jdbcType);

    System.out.println("AV56dSB1NR :: dataSource = " + dataSource);

    AtomicLong time = new AtomicLong(1000);

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

      @Override
      protected long autoResetTimeout() {
        return 3000;
      }

      @Override
      protected long currentTimeMillis() {
        return time.get();
      }
    };

    //
    //
    TestConfig config = configFactory.createConfig(TestConfig.class);
    //
    //

    config.strParam();

    DbRegister register = configFactory.register();

    String tableName = register.tableNameQuoted(tv.schema, "TestConfig");

    System.out.println("avz1rCLm6I :: tableName = " + tableName);

    TestDbAccess testDbAccess = TestDbAccess.of(dataSource);

    FieldNames fn = configFactory.fieldNames();

    String paramPath  = register.nameQuote(fn.paramPath());
    String paramValue = register.nameQuote(fn.paramValue());
    String modifiedAt = register.nameQuote(fn.modifiedAt());

    String asdValue = RND.strEng(10);

    String value = RND.strEng(11);

    testDbAccess.insertParam(tableName, paramPath, paramValue, "asd", asdValue, modifiedAt);
    testDbAccess.updateParam(tableName, paramPath, paramValue, "strParam", value);
    testDbAccess.incrementTimestampSec(tableName, modifiedAt, 3);

    time.addAndGet(10_000);

    assertThat(config.strParam()).isEqualTo(value);

    String asdActualValue = testDbAccess.readParam(tableName, paramPath, paramValue, "asd")
                                        .orElseThrow(RuntimeException::new);

    assertThat(asdActualValue).isEqualTo(asdValue);

    configFactory.reset();

    TestConfig2 config2 = configFactory.createConfig(TestConfig2.class);
    assertThat(config2.strParam()).isEqualTo(value);
  }
}
