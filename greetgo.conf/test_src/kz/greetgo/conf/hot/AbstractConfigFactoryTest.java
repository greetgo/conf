package kz.greetgo.conf.hot;

import kz.greetgo.conf.RND;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractConfigFactoryTest {

  @SuppressWarnings("InnerClassMayBeStatic")
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
  public void useConfigAsKeyOfMap() {
    final Testing testing = new Testing();

    HotConfig1 config11 = testing.createConfig(HotConfig1.class);
    HotConfig1 config12 = testing.createConfig(HotConfig1.class);

    HotConfig2 config21 = testing.createConfig(HotConfig2.class);
    HotConfig2 config22 = testing.createConfig(HotConfig2.class);

    Object value11 = RND.str(10);
    Object value12 = RND.str(10);
    Object value21 = RND.str(10);
    Object value22 = RND.str(10);

    Map<Object, Object> map = new HashMap<>();

    map.put(config11, value11);
    map.put(config12, value12);
    map.put(config21, value21);
    map.put(config22, value22);

    Object actualValue11 = map.get(config11);
    Object actualValue12 = map.get(config12);
    Object actualValue21 = map.get(config21);
    Object actualValue22 = map.get(config22);

    assertThat(actualValue11).isEqualTo(value11);
    assertThat(actualValue12).isEqualTo(value12);
    assertThat(actualValue21).isEqualTo(value21);
    assertThat(actualValue22).isEqualTo(value22);

  }

  // TODO pompei отладить этот тест
  @Test
  public void createInManyThreads() throws Exception {

    final Testing testing = new Testing();

    Thread[] tt = new Thread[10];

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

          config2.probe();
          config2.intProbe();
        }
      });
    }

    for (Thread t : tt) {
      t.start();
    }

    Thread.sleep(70);

    reading.set(false);

    for (Thread t : tt) {
      t.join();
    }

    System.out.println("uXN0T24S5A :: callCountOf"
                         + "\n\t\tLoadConfigContent      = " + testing.cs.callCountOfLoadConfigContent()
                         + "\n\t\tIsConfigContentExists  = " + testing.cs.callCountOfIsConfigContentExists()
                         + "\n\t\tGetLastChangedAt       = " + testing.cs.callCountOfGetLastChangedAt()
                         + "\n\t\tSaveConfigContent      = " + testing.cs.callCountOfSaveConfigContent()
    );

    assertThat(testing.cs.callCountOfLoadConfigContent()).describedAs("callCountOfLoadConfigContent")
                                                         .isEqualTo(2);

    assertThat(testing.cs.callCountOfIsConfigContentExists()).describedAs("callCountOfIsConfigContentExists")
                                                             .isEqualTo(0);

    assertThat(testing.cs.callCountOfGetLastChangedAt()).describedAs("callCountOfGetLastChangedAt")
                                                        .isEqualTo(2);

    assertThat(testing.cs.callCountOfSaveConfigContent()).describedAs("callCountOfSaveConfigContent")
                                                         .isEqualTo(2);

  }

  @Test
  public void checkArrays_new() {
    final Testing testing = new Testing();

    HostConfigWithLists config = testing.createConfig(HostConfigWithLists.class);

    assertThat(config.elementA().intField()).isEqualTo(20019);
    assertThat(config.elementA().strField()).isEqualTo("By one");

    String content = testing.cs.loadConfigContent("HostConfigWithLists.txt");
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
                                    "status=0");
  }

  @Test
  public void checkArrays_hasContent() {
    final Testing testing = new Testing();

    testing.cs.saveConfigContent("HostConfigWithLists.txt", "" +
                                                              "elementB.0.intField = 45000\n" +
                                                              "elementB.0.strField = The new begins STORED\n" +
                                                              "elementB.1.intField = 456\n" +
                                                              "elementB.1.strField = hello world\n" +
                                                              "\n" +
                                                              "elementA.intField = 709\n" +
                                                              "");

    HostConfigWithLists config = testing.createConfig(HostConfigWithLists.class);

    assertThat(config.elementA().intField()).isEqualTo(709);
    assertThat(config.elementA().strField()).isEqualTo("By one");

    assertThat(config.elementB().get(0).intField()).isEqualTo(45_000);
    assertThat(config.elementB().get(0).strField()).isEqualTo("The new begins STORED");
    assertThat(config.elementB().get(1).intField()).isEqualTo(456);
    assertThat(config.elementB().get(1).strField()).isEqualTo("hello world");

    String content = testing.cs.loadConfigContent("HostConfigWithLists.txt");

    System.out.println("v36nRz56uV :: content=\n" + content);

    content = Arrays.stream(content.split("\n"))
                    .filter(s -> s.trim().length() > 0)
                    .filter(s -> !s.trim().startsWith("#"))
                    .sorted()
                    .collect(Collectors.joining("\n"));

    assertThat(content).isEqualTo("" +
                                    "elementA.intField = 709\n" +
                                    "elementA.strField=By one\n" +
                                    "elementB.0.intField = 45000\n" +
                                    "elementB.0.strField = The new begins STORED\n" +
                                    "elementB.1.intField = 456\n" +
                                    "elementB.1.strField = hello world\n" +
                                    "status=0");
  }

  @Test
  public void defaultListSize_new_reset_exists() {
    final Testing testing = new Testing();

    HotConfigWithDefaultListSize config = testing.createConfig(HotConfigWithDefaultListSize.class);

    assertThat(config.longList()).hasSize(9);
    assertThat(config.classList()).hasSize(7);

    String location = testing.configLocationFor(HotConfigWithDefaultListSize.class);

    String content = Arrays.stream(testing.cs.loadConfigContent(location).split("\n"))
                           .filter(s -> s.trim().length() > 0)
                           .filter(s -> !s.trim().startsWith("#"))
                           .sorted()
                           .collect(Collectors.joining("\n"));

    assertThat(content).isEqualTo("classList.0.intField=20019\n" +
                                    "classList.0.strField=By one\n" +
                                    "classList.1.intField=20019\n" +
                                    "classList.1.strField=By one\n" +
                                    "classList.2.intField=20019\n" +
                                    "classList.2.strField=By one\n" +
                                    "classList.3.intField=20019\n" +
                                    "classList.3.strField=By one\n" +
                                    "classList.4.intField=20019\n" +
                                    "classList.4.strField=By one\n" +
                                    "classList.5.intField=20019\n" +
                                    "classList.5.strField=By one\n" +
                                    "classList.6.intField=20019\n" +
                                    "classList.6.strField=By one\n" +
                                    "longList.0=70078\n" +
                                    "longList.1=70078\n" +
                                    "longList.2=70078\n" +
                                    "longList.3=70078\n" +
                                    "longList.4=70078\n" +
                                    "longList.5=70078\n" +
                                    "longList.6=70078\n" +
                                    "longList.7=70078\n" +
                                    "longList.8=70078");

    testing.cs.saveConfigContent(location, "classList.2.strField=Boom loon hi\n" +
                                             "classList.5.intField=119988\n" +
                                             "longList.4=4511\n");

    assertThat(config.longList()).hasSize(9);
    assertThat(config.classList()).hasSize(7);

    String content2 = Arrays.stream(testing.cs.loadConfigContent(location).split("\n"))
                            .filter(s -> s.trim().length() > 0)
                            .filter(s -> !s.trim().startsWith("#"))
                            .sorted()
                            .collect(Collectors.joining("\n"));

    assertThat(content2).isEqualTo("classList.0.intField=20019\n" +
                                     "classList.0.strField=By one\n" +
                                     "classList.1.intField=20019\n" +
                                     "classList.1.strField=By one\n" +
                                     "classList.2.intField=20019\n" +
                                     "classList.2.strField=Boom loon hi\n" +
                                     "classList.3.intField=20019\n" +
                                     "classList.3.strField=By one\n" +
                                     "classList.4.intField=20019\n" +
                                     "classList.4.strField=By one\n" +
                                     "classList.5.intField=119988\n" +
                                     "classList.5.strField=By one\n" +
                                     "classList.6.intField=20019\n" +
                                     "classList.6.strField=By one\n" +
                                     "longList.0=70078\n" +
                                     "longList.1=70078\n" +
                                     "longList.2=70078\n" +
                                     "longList.3=70078\n" +
                                     "longList.4=4511\n" +
                                     "longList.5=70078\n" +
                                     "longList.6=70078\n" +
                                     "longList.7=70078\n" +
                                     "longList.8=70078");
  }
}
