## Quick start guide storing config data in files

### 1. Add the following maven dependency:

```
  <dependency>
    <groupId>kz.greetgo</groupId>
    <artifactId>greetgo.conf</artifactId>
    <version>2.0.0</version>
  </dependency>
```
Or if you use gradle:
```
    compile "kz.greetgo:greetgo.conf:2.0.0"
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
import kz.greetgo.conf.hot.FileConfigFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class MyConfigFactory extends FileConfigFactory {
  @Override
  protected Path getBaseDir() {
    return Paths.get("/path/to/directory/where/config/files/are/located");
  }

  @Override
  protected String getConfigFileExt() {
    return ".hotconfig";
  }

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

    System.out.println("config1. strParam() = " + config1. strParam());
    System.out.println("config1.longParam() = " + config1.longParam());
    System.out.println("config2. intParam() = " + config2. intParam());

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

    System.out.println("config1. strParam() = " + config1. strParam());
    System.out.println("config1.longParam() = " + config1.longParam());
    System.out.println("config2. intParam() = " + config2. intParam());
  }

}
```

You can create config files manually. Or you can let the system to create config files automatically.
In this situation the system will use for parameters the default values - values
from annotations @Default(Str|Int|...)Value.
