### Features

#### Base types in method returns of the config interface 

You can use the following types in method returns of the config interface:

```java
public interface SomeConfig {

  String param1();

  int param2();

  Integer param3();

  long param4();

  Long param5();

  double param6();

  Double param7();

  float param8();

  Float param9();

  BigDecimal param10();

  BigInteger param11();

  SomeEnum param12();
}
```

#### Lazy config file creation OR annotation @ForcibleInit

Look two fragments of code:

```
/* Line 1 */ SomeConfig config = configFactory.createConfig(SomeConfig.class);
```

```
/* Line 2 */ config.someParameter();
```

Line 1 creates implementation of config interface.

Line 2 reads some parameter from config file at the first time after line 1.

If config file is absent, then the system creates config file at line 2, not at line 1.

If you want the system creates config file at line 1, then you need mark the config interface by annotation

    kz.greetgo.conf.hot.ForcibleInit

### Data types in method returns of the config interface

Also, you can use data class as parameter type. For example, let you have data class:

```java
public class SomeModel {
  public int field1;
  public String field2;
}
```

Then you can use config interface:

```java
public interface ConfigInterface {
  SomeModel param1();
  SomeModel param2();
}
```

Then config file looks like:

```text
param1.field1=11
param1.field2=txt1
param2.field1=22
param2.field2=txt2
```
