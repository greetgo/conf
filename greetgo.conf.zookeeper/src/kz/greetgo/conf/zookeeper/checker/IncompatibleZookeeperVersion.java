package kz.greetgo.conf.zookeeper.checker;

public class IncompatibleZookeeperVersion extends RuntimeException {
  public IncompatibleZookeeperVersion() {
    super("Illegal version of zookeeper. Please use version 3.6.0+");
  }
}
