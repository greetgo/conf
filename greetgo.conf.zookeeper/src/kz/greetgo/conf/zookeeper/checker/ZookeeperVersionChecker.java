package kz.greetgo.conf.zookeeper.checker;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.KeeperException;

public class ZookeeperVersionChecker {

  public static void check(String zookeeperConnectionString) {
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    try (CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy)) {
      client.start();
      client.blockUntilConnected();

      client.watchers()
            .add()
            .withMode(AddWatchMode.PERSISTENT_RECURSIVE)
            .usingWatcher((CuratorWatcher) event -> {})
            .forPath("/test/version/3.6.0");

    } catch (KeeperException.UnimplementedException e) {
      throw new IncompatibleZookeeperVersion();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
