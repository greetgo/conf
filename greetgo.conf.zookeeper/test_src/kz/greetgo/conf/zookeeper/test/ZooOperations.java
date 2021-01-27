package kz.greetgo.conf.zookeeper.test;

import kz.greetgo.util.RND;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.testng.SkipException;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ZooOperations {
  private final String connectionStr;

  public ZooOperations(String connectionStr) {
    this.connectionStr = connectionStr;
  }

  public String connectionStr() {
    return connectionStr;
  }

  private CuratorFramework client() {
    CuratorFramework ret = CuratorFrameworkFactory.newClient(connectionStr, new ExponentialBackoffRetry(1000, 3));
    ret.start();
    return ret;
  }

  public String readFile(String pathToFile) {
    try (CuratorFramework client = client()) {

      byte[] bytes = client.getData().forPath(pathToFile);
      if (bytes == null) return null;
      return new String(bytes, UTF_8);

    } catch (KeeperException.NoNodeException e) {
      return null;
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void writeFile(String pathToFile, String content) {
    try (CuratorFramework client = client()) {

      if (content == null) {
        client.delete().forPath(pathToFile);
      } else {
        client.create().orSetData().creatingParentsIfNeeded().forPath(pathToFile, content.getBytes(UTF_8));
      }

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private final AtomicBoolean connectChecked = new AtomicBoolean(false);
  private final AtomicBoolean hasConnect     = new AtomicBoolean(false);

  public void skipTestIfNoZoo() {
    if (connectChecked.get()) {
      if (hasConnect.get()) return;
      throw new SkipException("uwrK2NzDIz :: No zookeeper");
    }

    try (CuratorFramework client = CuratorFrameworkFactory.newClient(
      connectionStr, 400, 100, new ExponentialBackoffRetry(10, 0))) {

      client.start();

      assertThat(client).isNotNull();

      client.getData().forPath("/" + RND.strEng(30));

      connectChecked.set(true);
      hasConnect.set(true);
    } catch (KeeperException.ConnectionLossException e) {
      connectChecked.set(true);
      hasConnect.set(false);
      throw new SkipException("jxZhcGL1N4 :: No zookeeper");
    } catch (KeeperException.NoNodeException e) {
      connectChecked.set(true);
      hasConnect.set(true);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

}
