package kz.greetgo.conf.zookeeper;

import kz.greetgo.conf.core.ConfAccess;
import kz.greetgo.conf.hot.AbstractHotConfFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.requireNonNull;

public abstract class AbstractZookeeperConfigFactory extends AbstractHotConfFactory implements AutoCloseable {
  protected abstract String zooConnectionString();

  protected int zooSessionTimeoutMs() {
    return 60 * 1000;
  }

  protected int zooConnectionTimeoutMs() {
    return 15 * 1000;
  }

  protected RetryPolicy zooRetryPolicy() {
    return new ExponentialBackoffRetry(1000, 3);
  }

  private final AtomicReference<CuratorFramework> client = new AtomicReference<>(null);

  private final ReentrantLock locker = new ReentrantLock();

  private CuratorFramework client() {

    {
      CuratorFramework ret = client.get();
      if (ret != null) return ret;
    }

    locker.lock();
    try {
      {
        CuratorFramework ret = client.get();
        if (ret != null) return ret;
      }

      CuratorFramework ret = CuratorFrameworkFactory.newClient(zooConnectionString(),
                                                               zooSessionTimeoutMs(),
                                                               zooConnectionTimeoutMs(),
                                                               zooRetryPolicy());

      ret.start();

      client.set(ret);

      return ret;
    } finally {
      locker.unlock();
    }
  }

  @Override
  public void close() {
    CuratorFramework curatorFramework = client.getAndSet(null);
    if (curatorFramework != null) {
      curatorFramework.close();
    }
  }

  protected abstract String baseDir();

  protected String getConfigFileExt() {
    return ".hotconfig";
  }

  protected <T> String extractConfigPath(Class<T> configInterface) {
    return slashing(baseDir()) + extractInterfaceName(configInterface) + getConfigFileExt();
  }

  private static String slashing(String baseDir) {
    requireNonNull(baseDir, "baseDir");
    boolean startedWithSlash  = baseDir.startsWith("/");
    boolean finishedWithSlash = baseDir.endsWith("/");
    if (startedWithSlash) {
      if (finishedWithSlash) {
        return baseDir;
      }
      return baseDir + "/";
    }
    if (finishedWithSlash) {
      return "/" + baseDir;
    }
    return "/" + baseDir + "/";
  }

  @Override
  protected <T> ConfAccess confAccess(Class<T> configInterface) {
    return new ZookeeperConfAccess(extractConfigPath(configInterface), this::client, this::confContentSerializer);
  }

  protected long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  /**
   * Defines auto reset timeout. It is a time interval in milliseconds to check last config modification date and time.
   * And if the date and time was changed, then it calls method `reset` for this config.
   *
   * @return auto reset timeout. Zero - auto resetting is off
   */
  protected long autoResetTimeout() {
    return 3000;
  }

}
