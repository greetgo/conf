package kz.greetgo.conf.hot;

import kz.greetgo.conf.RND;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractConfigFactoryTest {

  public static ConfigStorageForTests look;

  static class TestingFactory extends AbstractConfigFactory {
    final ConfigStorageForTests cs = new ConfigStorageForTests();

    TestingFactory() {
      look = cs;
    }

    @Override
    protected ConfigStorage getConfigStorage() {
      return cs;
    }

    @Override
    protected <T> String configLocationFor(Class<T> configInterface) {
      return configInterface.getSimpleName() + ".txt";
    }

    LongSupplier currentTimeMillis = System::currentTimeMillis;

    @Override
    protected long currentTimeMillis() {
      return currentTimeMillis.getAsLong();
    }

    @Override
    protected long autoResetTimeout() {
      return 3000;
    }
  }

  @Test
  public void useConfigAsKeyOfMap() {
    final TestingFactory tf = new TestingFactory();

    HotConfig1 config1 = tf.createConfig(HotConfig1.class);

    HotConfig2 config2 = tf.createConfig(HotConfig2.class);

    Object value1 = RND.str(10);
    Object value2 = RND.str(10);

    Map<Object, Object> map = new HashMap<>();

    map.put(config1, value1);
    map.put(config2, value2);

    Object actualValue1 = map.get(config1);
    Object actualValue2 = map.get(config2);

    assertThat(actualValue1).isEqualTo(value1);
    assertThat(actualValue2).isEqualTo(value2);

  }

  @Test
  public void createInManyThreads() throws Exception {

    AtomicLong time = new AtomicLong(1000);

    final TestingFactory tf = new TestingFactory();
    tf.currentTimeMillis = time::get;

    Thread[] tt = new Thread[11];

    final AtomicBoolean reading = new AtomicBoolean(true);

    for (int i = 0; i < tt.length; i++) {
      tt[i] = new Thread(() -> {
        HotConfig1 config1 = tf.createConfig(HotConfig1.class);
        HotConfig2 config2 = tf.createConfig(HotConfig2.class);

        int j = 0;

        while (j < 10) {
          config1.boolExampleValue();
          config1.intExampleValue();
          config1.intExampleValue2();
          config1.strExampleValue();

          config2.probe();
          config2.intProbe();

          if (reading.get()) {
            continue;
          }
          j++;
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
                         + "\n\t\tGetLastChangedAt = " + tf.cs.callCountOfGetLastChangedAt()
                         + "\n\t\tIsExists         = " + tf.cs.callCountOfIsExists()
                         + "\n\t\tLoad             = " + tf.cs.callCountOfLoad()
                         + "\n\t\tSave             = " + tf.cs.callCountOfSave()
    );

    assertThat(tf.cs.callCountOfGetLastChangedAt()).describedAs("callCount of GetLastChangedAt")
                                                   .isEqualTo(4);

    assertThat(tf.cs.callCountOfIsExists()).describedAs("callCount of IsExists")
                                           .isEqualTo(0);

    assertThat(tf.cs.callCountOfLoad()).describedAs("callCount of Load")
                                       .isEqualTo(2);

    assertThat(tf.cs.callCountOfSave()).describedAs("callCount of Save")
                                       .isEqualTo(2);

  }

  @Test
  public void manyCallsInOneThread() {
    AtomicLong currentTimeMillis = new AtomicLong(1000);

    final TestingFactory tf = new TestingFactory();
    tf.currentTimeMillis = currentTimeMillis::get;

    HotConfig1 config1 = tf.createConfig(HotConfig1.class);
    HotConfig2 config2 = tf.createConfig(HotConfig2.class);

    for (int i = 0; i < 10; i++) {

      config1.boolExampleValue();
      config1.intExampleValue();
      config1.intExampleValue2();
      config1.strExampleValue();

      config2.probe();
      config2.intProbe();
    }

    System.out.println("OeGz0v6rs9 :: callCountOf"
                         + "\n\t\tLoadConfigContent      = " + tf.cs.callCountOfLoad()
                         + "\n\t\tIsConfigContentExists  = " + tf.cs.callCountOfIsExists()
                         + "\n\t\tGetLastChangedAt       = " + tf.cs.callCountOfGetLastChangedAt()
                         + "\n\t\tSaveConfigContent      = " + tf.cs.callCountOfSave()
    );

    assertThat(tf.cs.callCountOfLoad()).describedAs("callCountOfLoadConfigContent")
                                       .isEqualTo(2);

    assertThat(tf.cs.callCountOfIsExists()).describedAs("callCountOfIsConfigContentExists")
                                           .isEqualTo(0);

    assertThat(tf.cs.callCountOfGetLastChangedAt()).describedAs("callCountOfGetLastChangedAt")
                                                   .isEqualTo(4);

    assertThat(tf.cs.callCountOfSave()).describedAs("callCountOfSaveConfigContent")
                                       .isEqualTo(2);

  }

  @SuppressWarnings("unused")
  @Description("Горячие конфиги ФИКС\n" //
                 + "Начинается новый день\n" //
                 + "И машины туда-сюда")
  public interface TestConfig {
    @Description("Пример описания")
    @DefaultStrValue(value = "def value for strExampleValue")
    String strExampleValue();

    @Description("Пример описания intExampleValue")
    int intExampleValue();

    @Description("Пример описания intExampleValue")
    @DefaultIntValue(349)
    int intExampleValue2();

    @Description("Пример описания boolExampleValue")
    @DefaultBoolValue(true)
    boolean boolExampleValue();

    @DefaultStrValue("Привет T1001")
    String name();
  }

  @Test
  public void createConfig__noReadOnCreating() {

    AtomicLong currentTimeMillis = new AtomicLong(1000);

    final TestingFactory tf = new TestingFactory();
    tf.currentTimeMillis = currentTimeMillis::get;

    //
    //
    TestConfig config = tf.createConfig(TestConfig.class);
    //
    //

    System.out.println("Z06U35ukE7 :: config.toString() = " + config.toString());
    System.out.println("Z06U35ukE7 :: identityHashCode(config) = " + System.identityHashCode(config));

    assertThat(config.toString()).endsWith("@" + System.identityHashCode(config));
    assertThat(config.toString()).contains(TestConfig.class.getName());

    assertThat(tf.cs.configLocations()).isEmpty();

    String name = config.name();

    assertThat(tf.cs.configLocations()).contains("TestConfig.txt");

    System.out.println("aD0BH9nF0K ::\n" + tf.cs.getContent("TestConfig.txt"));

    assertThat(name).isEqualTo("Привет T1001");
  }

  @Test
  public void checkArrays_new() {
    final TestingFactory tf = new TestingFactory();

    HostConfigWithLists config = tf.createConfig(HostConfigWithLists.class);

    assertThat(config.elementA().intField()).isEqualTo(20019);
    assertThat(config.elementA().strField()).isEqualTo("By one");

    String content = tf.cs.loadConfigContent("HostConfigWithLists.txt");
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
    final TestingFactory tf = new TestingFactory();

    tf.cs.saveConfigContent("HostConfigWithLists.txt", "" +
                                                         "elementB.0.intField = 45000\n" +
                                                         "elementB.0.strField = The new begins STORED\n" +
                                                         "elementB.1.intField = 456\n" +
                                                         "elementB.1.strField = hello world\n" +
                                                         "\n" +
                                                         "elementA.intField = 709\n" +
                                                         "");

    HostConfigWithLists config = tf.createConfig(HostConfigWithLists.class);

    assertThat(config.elementA().intField()).isEqualTo(709);
    assertThat(config.elementA().strField()).isEqualTo("By one");

    assertThat(config.elementB().get(0).intField()).isEqualTo(45_000);
    assertThat(config.elementB().get(0).strField()).isEqualTo("The new begins STORED");
    assertThat(config.elementB().get(1).intField()).isEqualTo(456);
    assertThat(config.elementB().get(1).strField()).isEqualTo("hello world");

    String content = tf.cs.loadConfigContent("HostConfigWithLists.txt");

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
    AtomicLong time = new AtomicLong(1000);

    final TestingFactory tf = new TestingFactory();
    tf.currentTimeMillis = time::get;

    HotConfigWithDefaultListSize config = tf.createConfig(HotConfigWithDefaultListSize.class);

    assertThat(config.longList()).hasSize(9);
    assertThat(config.classList()).hasSize(7);

    String location = tf.configLocationFor(HotConfigWithDefaultListSize.class);

    String content = Arrays.stream(tf.cs.loadConfigContent(location).split("\n"))
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

    tf.cs.saveConfigContent(location, "classList.2.strField=Boom loon hi\n" +
                                        "classList.5.intField=119988\n" +
                                        "longList.4=4511\n");

    time.addAndGet(5000);

    assertThat(config.longList()).hasSize(5);
    assertThat(config.classList()).hasSize(6);

    String content2 = Arrays.stream(tf.cs.loadConfigContent(location).split("\n"))
                            .filter(s -> s.trim().length() > 0)
                            .filter(s -> !s.trim().startsWith("#"))
                            .sorted()
                            .collect(Collectors.joining("\n"));

    assertThat(content2).isEqualTo("classList.2.strField=Boom loon hi\n" +
                                     "classList.5.intField=119988\n" +
                                     "longList.4=4511");

    tf.cs.saveConfigContent(location, "longList.1=98\n" +
                                        "longList.0=11\n" +
                                        "longList.3=17\n");

  }
}
