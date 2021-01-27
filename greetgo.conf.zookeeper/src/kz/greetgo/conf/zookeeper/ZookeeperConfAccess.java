package kz.greetgo.conf.zookeeper;

import kz.greetgo.conf.core.ConfAccess;
import kz.greetgo.conf.core.ConfContent;
import kz.greetgo.conf.core.ConfContentSerializer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.Date;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ZookeeperConfAccess implements ConfAccess {
  private final String configPath;
  private final Supplier<CuratorFramework> client;
  private final Supplier<ConfContentSerializer> confContentSerializer;

  public ZookeeperConfAccess(String configPath, Supplier<CuratorFramework> client,
                             Supplier<ConfContentSerializer> confContentSerializer) {
    this.configPath = configPath;
    this.client     = client;
    this.confContentSerializer = confContentSerializer;
  }

    @Override
    public ConfContent load() {
    try {

      byte[] bytes = client.get().getData().forPath(configPath);
      if (bytes == null) return null;
      String content = new String(bytes, UTF_8);
      return confContentSerializer.get().deserialize(content);

    } catch (KeeperException.NoNodeException e) {
      return null;
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

    @Override
    public void write(ConfContent confContent) {
    try {

      if (confContent == null) {
        client.get().delete().forPath(configPath);
      } else {
        String serializedString = confContentSerializer.get().serialize(confContent);
        byte[] contentBytes     = serializedString.getBytes(UTF_8);

        client.get().create().orSetData().creatingParentsIfNeeded().forPath(configPath, contentBytes);
      }

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

    @Override
    public Date lastModifiedAt() {
    try {

      Stat stat = client.get().checkExists().forPath(configPath);
      if (stat == null) return null;
      return new Date(stat.getMtime());

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
