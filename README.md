## Configuration through java interfaces

### References

- [Quick start guide storing config data in files](doc/001_quick_start_guide__files.md)

- [Quick start guide storing config data in SQL DB (JDBC)](doc/002_quick_start_guide__jdbc.md)

- [Quick start guide storing config data in Zookeeper](doc/003_quick_start_guide__zookeeper.md)

- [How easy to realize your own config storage](doc/004_realize_own_config_storage.md)

- [Features](doc/005_features.md)

### Small preamble

Every application needs different configuration information. Reading this information is always hard.

You can simplify it very much.

What if you want simply @Autowire some config and use it. Something like in the following code:

```java

@Component
public class SomeYourSpringBeanComponent {

  @Autowire
  private ConnectionConfig config;

  public void yourCoolMethod() {

    Connection connection = DriverManager.getConnection(
      "jdbc:postgresql://" + config.host() + "/" + config.databaseName(),
      config.username(), config.password());
    
    doSomeWithConnection(connection);
    
  }

}

```

To do it, you need create java interface (configuration java interface):

```java
@Description("Here you describe config file")
interface ConnectionConfig {
  @Description("Here you describe this config parameter")
  String host();

  @DefaultIntValue(5432)
  int port();

  String username();

  String password();

  String databaseName();

}
```

Do some spring magic:

```java

@Component
public class MagicConfigFactory extends SomeAbstractConfigFactory {

  @Override
  protected Path getBaseDir() {
    return Paths.get("/some/specified/directory/where/config/files/are/located");
  }
  
  @Override
  protected String getConfigFileExt() {
    return ".hotconfig";
  }

  @Bean
  public ConnectionConfig createConnectionConfig() {
    return createConfig(ConnectionConfig.class);
  }

}

```

Create file `ConnectionConfig.hostconfig` in specified directory with the following content:

```
host=remote.postgres.domain.com
port=5432
username=neo
password=Louk1FjpdUNjZB3I961As3NiOdHq0Z
databaseName=matrix
```

That's all. It's simple. Isn't it?

Moreover, you can change config file, and the system automatically detect it and reread config information
without restarting application.

Also, you can do NOT create config file by yourself manually - system creates the file automatically, and fill
parameters with default values, witch specified by annotations @Default(Str|Int|Long|...)Value
in the configuration java interface.

### How to do it?

- [Quick start guide storing config data in files](doc/001_quick_start_guide__files.md)

- [Quick start guide storing config data in SQL DB (JDBC)](doc/002_quick_start_guide__jdbc.md)

- [Quick start guide storing config data in Zookeeper](doc/003_quick_start_guide__zookeeper.md)

- [How easy to realize your own config storage](doc/004_realize_own_config_storage.md)

- [Features](doc/005_features.md)
