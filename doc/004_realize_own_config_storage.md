### Realization of your own config storage

To realize your own factories of config files, there is special class - AbstractHotConfFactory.

    kz.greetgo.conf.hot.AbstractHotConfFactory

In this class you need override one abstract method - confAccess(). This shown in the following code:

```java
import kz.greetgo.conf.hot.AbstractHotConfFactory;
import kz.greetgo.conf.core.ConfContent;

public abstract class AbstractMyConfigFactory extends AbstractHotConfFactory {

  protected <T> ConfAccess confAccess(Class<T> configInterface) {
    String interfaceName = extractInterfaceName(configInterface);
    ConfContentSerializer serializer = confContentSerializer();
    
    return new ConfAccess() {
      @Override
      public ConfContent load() {
        String strContent = readConfigContent(interfaceName);
        if (strContent == null) return null;
        return serializer.deserialize(strContent);
      }

      @Override
      public void write(ConfContent confContent) {
        if (confContent == null) {
          removeConfig(interfaceName);
        } else {
          String strContent = serializer.serialize(confContent);
          writeConfigContent(interfaceName, strContent);
        }
      }

      @Override
      public Date lastModifiedAt() {
        return readLastModifiedTimeOrNullIfItIsAbsent(interfaceName);
      }
    };
  }
  
}
```

Magic method `AbstractHotConfFactory.createConfig(interfaceClass)` calls
method `AbstractHotConfFactory.confAccess(interfaceClass)` and works with it.


