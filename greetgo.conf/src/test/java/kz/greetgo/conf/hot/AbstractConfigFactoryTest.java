package kz.greetgo.conf.hot;

import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.fest.assertions.api.Assertions.assertThat;

public class AbstractConfigFactoryTest {

  class Testing extends AbstractConfigFactory {
    final ConfigStorageForTests cs = new ConfigStorageForTests();

    @Override
    protected ConfigStorage getConfigStorage() {
      return cs;
    }

    @Override
    protected <T> String configLocationFor(Class<T> configInterface) {
      return configInterface.getSimpleName() + ".txt";
    }
  }

  @Test
  public void createInManyThreads() throws Exception {

    final Testing testing = new Testing();

    Thread tt[] = new Thread[10];

    final AtomicBoolean reading = new AtomicBoolean(true);

    for (int i = 0; i < tt.length; i++) {
      tt[i] = new Thread() {
        @Override
        public void run() {
          HotConfig1 config1 = testing.createConfig(HotConfig1.class);
          HotConfig2 config2 = testing.createConfig(HotConfig2.class);

          while (reading.get()) {
            config1.boolExampleValue();
            config1.intExampleValue();
            config1.intExampleValue2();
            config1.strExampleValue();

            config2.asd();
            config2.intAsd();
          }
        }
      };
    }

    for (Thread t : tt) {
      t.start();
    }

    for (int i = 0; i < 10; i++) {
      Thread.sleep(70);
      testing.reset();
    }

    Thread.sleep(70);

    reading.set(false);

    for (Thread t : tt) {
      t.join();
    }

//    System.out.println("callCountOfLoadConfigContent = " + testing.cs.callCountOfLoadConfigContent);
//    System.out.println("callCountOfIsConfigContentExists = " + testing.cs.callCountOfIsConfigContentExists);
//    System.out.println("callCountOfSaveConfigContent = " + testing.cs.callCountOfSaveConfigContent);

    assertThat(testing.cs.callCountOfLoadConfigContent.get()).isEqualTo(20);
    assertThat(testing.cs.callCountOfIsConfigContentExists.get()).isEqualTo(22);
    assertThat(testing.cs.callCountOfSaveConfigContent.get()).isEqualTo(2);

  }
}