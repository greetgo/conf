package kz.greetgo.conf.probes;

public class FactoryProbe {
  public static void main(String[] args) {
    MyConfigFactory factory = new MyConfigFactory();

    MyMigrationConfig config   = factory.createConfig(MyMigrationConfig.class);
    PostgresConfig    pgConfig = factory.createConfig(PostgresConfig.class);

    System.out.println("P00B3jkSTT :: config.batchSize() = " + config.batchSize());
    System.out.println("P00B3jkSTT :: config.updateTimeoutMs() = " + config.updateTimeoutMs());

    System.out.println("7MjmYMGhQE :: pgConfig = " + pgConfig.host());
    System.out.println("7MjmYMGhQE :: pgConfig = " + pgConfig.port());
    System.out.println("7MjmYMGhQE :: pgConfig = " + pgConfig.schema());
    System.out.println("7MjmYMGhQE :: pgConfig = " + pgConfig.password());
    System.out.println("7MjmYMGhQE :: pgConfig = " + pgConfig.username());

  }
}
