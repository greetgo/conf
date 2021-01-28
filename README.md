# Configuration through java interfaces

Every application needs different configuration information. Reading this information is always hard.

You can simplify it very much.

What if you want simply autowire some configuration and use it like in the following code:

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

To do it, you need create java interface:

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

Make small spring magic:

```java

@Component
public class MagicConfigFactory extends SomeAbstractConfigFactory {

  @Override
  protected Path getBaseDir() {
    return Paths.get("/some/specified/directory/where/config/files/are/located");
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

That's all - simple.

Moreover, you can change config file, and the system automatically detect it and reread config information
without restarting application.
