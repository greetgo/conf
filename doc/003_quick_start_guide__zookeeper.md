## Quick start guide storing config data in Zookeeper

### 1. Add the following maven dependencies:

```
  <dependency>
    <groupId>kz.greetgo</groupId>
    <artifactId>greetgo.conf</artifactId>
    <version>2.0.0</version>
  </dependency>
  <dependency>
    <groupId>kz.greetgo</groupId>
    <artifactId>greetgo.conf.zookeeper</artifactId>
    <version>2.0.0</version>
  </dependency>
```

Or if you use gradle:

```
    compile "kz.greetgo:greetgo.conf:2.0.0"
    compile "kz.greetgo:greetgo.conf.zookeeper:2.0.0"
```

### 2. Create config java interfaces, you need to use in your application.

For example:

```java
import kz.greetgo.conf.hot.Description;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.DefaultLongValue;

@Description("The config file documentation")
public interface TestConfig1 {

  @Description("strParam description")
  @DefaultStrValue("The default str param value")
  String strParam();

  @DefaultLongValue(4535236L)
  long longParam();
}
```

```java
import kz.greetgo.conf.hot.DefaultIntValue;

public interface TestConfig2 {

  @DefaultIntValue(4567)
  int intParam();

}
```

### 3. Create factory for config java interface implementation:

```java
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import kz.greetgo.conf.jdbc.JdbcConfigFactory;

@Component
public class MyConfigFactory extends AbstractZookeeperConfigFactory {

  @Override
  protected String zooConnectionString() {
    return "zookeeper-host:port, host:port";
  }

  @Override
  protected String baseDir() {
    return "/path/to/directory/in/zookeeper/where/config/files/are/located";
  }

  // here you can override another methods to change default settings

  @Bean
  public TestConfig1 createTestConfig1() {
    return createConfig(TestConfig1.class);
  }

  @Bean
  public TestConfig2 createTestConfig2() {
    return createConfig(TestConfig2.class);
  }

}
```

### 4. All ready. Now you can read your config files

```java

@Component
public class SomeYourSpringComponent {

  @Autowired
  private TestConfig1 config1;

  @Autowired
  private TestConfig2 config2;

  public void yourCoolMethod() {

    System.out.println("config1. strParam() = " + config1.strParam());
    System.out.println("config1.longParam() = " + config1.longParam());
    System.out.println("config2. intParam() = " + config2.intParam());

  }

}
```

Or if you do NOT use Spring Framework, you can do something like the following:

```java
public class SomeYourLauncher {

  public static void main(String[] args) {
    MyConfigFactory factory = new MyConfigFactory();

    TestConfig1 config1 = factory.createTestConfig1();
    TestConfig2 config2 = factory.createTestConfig2();

    System.out.println("config1. strParam() = " + config1.strParam());
    System.out.println("config1.longParam() = " + config1.longParam());
    System.out.println("config2. intParam() = " + config2.intParam());
  }

}
```

When you read config param for the first time, the system automatically creates config file in Zookeeper
with creating all parent directories. The config file will be filled with default param values,
read from annotations @Default(Str|Int|...)Value.

You can change values in this table, and the system automatically rereads config data without restarting your
application.
