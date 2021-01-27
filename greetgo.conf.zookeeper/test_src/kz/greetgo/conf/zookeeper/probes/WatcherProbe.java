package kz.greetgo.conf.zookeeper.probes;

import kz.greetgo.conf.zookeeper.checker.ZookeeperVersionChecker;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WatcherProbe {

  private final Path cuiDir              = Paths.get("build").resolve(getClass().getSimpleName()).resolve("cui");
  private final Path shutdownAppIfDelete = cuiDir.resolve("__shutdown_if_it_deleted__");

  public static void main(String[] args) throws Exception {
    new WatcherProbe().execute();
  }

  private void execute() throws Exception {
    shutdownAppIfDelete.toFile().getParentFile().mkdirs();
    shutdownAppIfDelete.toFile().createNewFile();

    String zookeeperConnectionString = "localhost:51078";

    ZookeeperVersionChecker.check(zookeeperConnectionString);

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    try (CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy)) {
      client.start();
      System.out.println("Fz0GG8WUEc :: client.start();");

      CuratorFrameworkState state = client.getState();
      System.out.println("LFdCa9tn5X :: state = " + state);

      work(client);
    }
  }

  private void work(CuratorFramework client) throws Exception {
    try {
      client.create()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.PERSISTENT)
            .forPath("/asd/wow", "Сила в мыле".getBytes(StandardCharsets.UTF_8));
    } catch (KeeperException.NodeExistsException ignore) {
    }

    client.watchers()
          .add()
          .withMode(AddWatchMode.PERSISTENT_RECURSIVE)
          .usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) {
              System.out.println("c8A8GknUuj :: event = " + event);
            }
          }).forPath("/asd/wow");

    circle();


  }

  private void circle() throws InterruptedException {
    while (true) {
      if (!Files.exists(shutdownAppIfDelete)) return;
      Thread.sleep(400);
    }
  }

}
