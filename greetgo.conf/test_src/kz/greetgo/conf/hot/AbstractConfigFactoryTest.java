package kz.greetgo.conf.hot;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
      tt[i] = new Thread(() -> {
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
      });
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

    assertThat(testing.cs.callCountOfLoadConfigContent.get()).isEqualTo(20);
    assertThat(testing.cs.callCountOfIsConfigContentExists.get()).isEqualTo(22);
    assertThat(testing.cs.callCountOfSaveConfigContent.get()).isEqualTo(2);

  }

  @Test
  public void checkArrays_new() throws Exception {
    final Testing testing = new Testing();

    HostConfigWithLists config = testing.createConfig(HostConfigWithLists.class);

    assertThat(config.elementA().intField).isEqualTo(20019);
    assertThat(config.elementA().strField).isEqualTo("By one");

    String content = testing.cs.contentMap.get("HostConfigWithLists.txt");
    content = Arrays.stream(content.split("\n"))
      .filter(s -> s.trim().length() > 0)
      .filter(s -> !s.trim().startsWith("#"))
      .sorted()
      .collect(Collectors.joining("\n"));

    assertThat(content).isEqualTo("" +
      "elementA.intField=20019\n" +
      "elementA.strField=By one\n" +
      "elementB.0.intField=20019\n" +
      "elementB.0.strField=By one\n" +
      "elementB.listElementsCount=1\n" +
      "status=0");
  }

  @Test
  public void checkArrays_hasContent() throws Exception {
    final Testing testing = new Testing();

    testing.cs.contentMap.put("HostConfigWithLists.txt", "" +
      "elementB.listElementsCount=3\n" +
      "elementB.0.intField = 45000\n" +
      "elementB.0.strField = The new begins\n" +
      "elementB.1.intField = 456\n" +
      "elementB.1.strField = hello world\n" +
      "\n" +
      "elementA.intField = 709\n" +
      ""
    );

    HostConfigWithLists config = testing.createConfig(HostConfigWithLists.class);

    assertThat(config.elementA().intField).isEqualTo(709);
    assertThat(config.elementA().strField).isEqualTo("By one");

    assertThat(config.elementB().get(0).intField).isEqualTo(45_000);
    assertThat(config.elementB().get(0).strField).isEqualTo("The new begins");
    assertThat(config.elementB().get(1).intField).isEqualTo(456);
    assertThat(config.elementB().get(1).strField).isEqualTo("hello world");
    assertThat(config.elementB().get(2).intField).isEqualTo(20019);
    assertThat(config.elementB().get(2).strField).isEqualTo("By one");

    String content = testing.cs.contentMap.get("HostConfigWithLists.txt");
    content = Arrays.stream(content.split("\n"))
      .filter(s -> s.trim().length() > 0)
      .filter(s -> !s.trim().startsWith("#"))
      .sorted()
      .collect(Collectors.joining("\n"));

    assertThat(content).isEqualTo("" +
      "elementA.intField = 709\n" +
      "elementA.strField=By one\n" +
      "elementB.0.intField = 45000\n" +
      "elementB.0.strField = The new begins\n" +
      "elementB.1.intField = 456\n" +
      "elementB.1.strField = hello world\n" +
      "elementB.2.intField=20019\n" +
      "elementB.2.strField=By one\n" +
      "elementB.listElementsCount=3\n" +
      "status=0");
  }
}