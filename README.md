# Interface configuration

Every application needs different configuration information. It's desirable to reread configuration without
restart application - hot configuration.

What if you create java interface:

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

Make some magic in spring application:

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

After you create file `ConnectionConfig.hostconfig` in specified directory with the following content:

```
host=remote.postgres.domain.com
port=5432
username=neo
password=Louk1FjpdUNjZB3I961As3NiOdHq0Z
databaseName=matrix
```

Now you can autowire this interface in any of your application bean component and reads config data
using code like this:

```java

@Component
public class SomeYouSpringBeanComponent {
  
  @Autowire
  private ConnectionConfig connectionConfig;
  
  public void yourCoolMethod() {
    
    System.out.println("username = " + connectionConfig.username());
    
  }
  
}

```

Also, if you change config file, the system automatically detect it and reread new config data
without the restart application.
