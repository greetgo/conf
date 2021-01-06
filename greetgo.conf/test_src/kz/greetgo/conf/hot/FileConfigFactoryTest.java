package kz.greetgo.conf.hot;

import kz.greetgo.conf.ConfData;
import kz.greetgo.conf.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileConfigFactoryTest {

  @Test
  public void createDefault() {
    {
      File f = new File("build/wow/HotConfig1.hot");
      if (f.exists()) f.delete();
    }

    TestHotConfigFab fab  = new TestHotConfigFab(Paths.get("build").resolve("wow"), ".hot");
    HotConfig1       conf = fab.createConfig1();

    assertThat(conf.intExampleValue()).isEqualTo(0);
    assertThat(conf.intExampleValue2()).isEqualTo(349);
    assertThat(conf.boolExampleValue()).isEqualTo(true);
    assertThat(conf.strExampleValue()).isEqualTo("def value for strExampleValue");
  }

  @Test
  public void createDefaultRest() throws Exception {
    {
      File f = new File("build/asd/HotConfig1.hot");
      if (f.exists()) f.delete();
      f.getParentFile().mkdirs();

      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 711");
      out.close();
    }

    TestHotConfigFab fab  = new TestHotConfigFab(Paths.get("build").resolve("asd"), ".hot");
    HotConfig1       conf = fab.createConfig1();

    assertThat(conf.intExampleValue()).isEqualTo(711);
    assertThat(conf.intExampleValue2()).isEqualTo(349);
    assertThat(conf.boolExampleValue()).isEqualTo(true);
    assertThat(conf.strExampleValue()).isEqualTo("def value for strExampleValue");
  }

  @Test
  public void readExists() throws Exception {
    {
      File f = new File("build/asd/HotConfig1.hot");
      if (f.exists()) f.delete();
      f.getParentFile().mkdirs();

      try (PrintStream out = new PrintStream(f, "UTF-8")) {
        out.println("intExampleValue = 7111");
        out.println("intExampleValue2 = 444");
        out.println("boolExampleValue = 0");
        out.println("strExampleValue = pa ra be lum");
      }
    }

    TestHotConfigFab fab  = new TestHotConfigFab(Paths.get("build").resolve("asd"), ".hot");
    HotConfig1       conf = fab.createConfig1();

    assertThat(conf.intExampleValue()).isEqualTo(7111);
    assertThat(conf.intExampleValue2()).isEqualTo(444);
    assertThat(conf.boolExampleValue()).isEqualTo(false);
    assertThat(conf.strExampleValue()).isEqualTo("pa ra be lum");
  }

  @Test
  public void readConfigParamsAfterFileChanged() throws Exception {

    File f = new File("build/asd/HotConfig1.hot");
    if (f.exists()) {
      f.delete();
    }
    f.getParentFile().mkdirs();

    {
      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 7111");
      out.println("intExampleValue2 = 444");
      out.println("boolExampleValue = 0");
      out.println("strExampleValue = status");
      out.close();
    }

    System.out.println("0dH548V7S7 :: getLastModifiedTime = " + Files.getLastModifiedTime(f.toPath()).toMillis());

    AtomicLong time = new AtomicLong(1000);

    TestHotConfigFab fab = new TestHotConfigFab(Paths.get("build").resolve("asd"), ".hot");
    fab.currentTimeMillis = time::get;

    HotConfig1 conf = fab.createConfig1();

    assertThat(conf.intExampleValue()).isEqualTo(7111);
    assertThat(conf.intExampleValue2()).isEqualTo(444);
    assertThat(conf.boolExampleValue()).isEqualTo(false);
    assertThat(conf.strExampleValue()).isEqualTo("status");

    Thread.sleep(70);

    {
      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 999");
      out.println("intExampleValue2 = 111");
      out.println("boolExampleValue = 1");
      out.println("strExampleValue = quantum");
      out.close();

      System.out.println("5Dhj4Pq543 :: getLastModifiedTime = " + Files.getLastModifiedTime(f.toPath()).toMillis());
    }

    time.addAndGet(5000);
    Thread.sleep(70);

    assertThat(conf.intExampleValue()).isEqualTo(999);
    assertThat(conf.intExampleValue2()).isEqualTo(111);
    assertThat(conf.boolExampleValue()).isEqualTo(true);
    assertThat(conf.strExampleValue()).isEqualTo("quantum");

  }

  public interface HotConfigParent {
    Integer parentInt();
  }

  public interface ConcreteHotConfig extends HotConfigParent {
    int concreteInt();
  }

  Random rnd = new Random();

  @Test
  public void parentingOfHotConfigs() throws Exception {

    final Path baseDir = Paths
                           .get("build")
                           .resolve("parentingOfHotConfigs_baseDir")
                           .resolve("" + rnd.nextInt(10_000_000));

    FileConfigFactory fileConfigFactory = new FileConfigFactory() {
      @Override
      protected Path getBaseDir() {
        return baseDir;
      }
    };

    {
      File file = fileConfigFactory.storageFileFor(ConcreteHotConfig.class);

      if (file.exists()) file.delete();
      file.getParentFile().mkdirs();
      try (PrintStream out = new PrintStream(file, "UTF-8")) {
        out.println("parentInt = 617283");
        out.println("concreteInt = 1874");
      }
    }


    ConcreteHotConfig config = fileConfigFactory.createConfig(ConcreteHotConfig.class);

    assertThat(config).isNotNull();
    assertThat(config.concreteInt()).isEqualTo(1874);
    assertThat(config.parentInt()).isEqualTo(617283);


  }

  @Test
  public void testingMethod_toString_inConfig() {
    final Path baseDir = Paths
                           .get("build")
                           .resolve("testingMethod_toString_inConfig")
                           .resolve("" + rnd.nextInt(10_000_000));

    FileConfigFactory fileConfigFactory = new FileConfigFactory() {
      @Override
      protected Path getBaseDir() {
        return baseDir;
      }
    };

    ConcreteHotConfig config = fileConfigFactory.createConfig(ConcreteHotConfig.class);


    assertThat(config.toString()).isNotNull();
    assertThat(config.toString()).isNotEqualTo("null");
    assertThat(config.toString()).contains(ConcreteHotConfig.class.getName());

  }

  @Test
  public void dynamicTest() {

    final Path   baseDir = Paths.get("build").resolve("dynamicTest");
    final String ext     = ".hot.txt";

    TestHotConfigFab fab = new TestHotConfigFab(baseDir, ext);

    HotConfig1 config1 = fab.createConfig1();

    HotConfig2 config2 = fab.createConfig2();

    System.out.println("config1.strExampleValue() = " + config1.strExampleValue());
    System.out.println("config2.asd() = " + config2.probe());
    System.out.println("config2.userEnv() = " + config2.userEnv());
    System.out.println("ok");
  }

  @Test
  public void initDefaultValuesOnCreate() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    final Path baseDir = Paths.get("build")
                              .resolve(getClass().getSimpleName())
                              .resolve("initDefaultValuesOnCreate");

    final String ext = ".hot." + sdf.format(new Date()) + '.' + RND.str(10) + ".txt";

    AllTypesConfigFactory factory = new AllTypesConfigFactory(baseDir, ext);

    //
    //
    factory.createAllTypesConfig();
    //
    //

    Path configFile = baseDir.resolve(AllTypesConfig.class.getSimpleName() + ext);

    System.out.println("6RTdUAj2pG :: configFile = " + configFile);

    assertThat(configFile).exists();

    ConfData confData = new ConfData();
    confData.readFromFile(configFile.toFile());

    assertThat(confData.str("  strValue     ".trim())).isEqualTo("  def-str-val WOW ".trim());
    assertThat(confData.str("  intValue     ".trim())).isEqualTo("  123             ".trim());
    assertThat(confData.str("  longValue    ".trim())).isEqualTo("  765876          ".trim());
    assertThat(confData.str("  boolValue1   ".trim())).isEqualTo("  true            ".trim());
    assertThat(confData.str("  boolValue2   ".trim())).isEqualTo("  false           ".trim());
    assertThat(confData.str("  doubleValue  ".trim())).isEqualTo("  123.072         ".trim());

  }

  @Test
  public void readConfig__allTypesConfig__readFromFile() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    final Path baseDir = Paths
                           .get("build")
                           .resolve("readConfig__allTypesConfig__readFromFile")
                           .resolve(sdf.format(new Date()));

    final String ext = ".hot." + RND.str(10) + ".txt";

    System.out.println("4f32c5fc :: baseDir = " + baseDir);

    Path configFile = baseDir.resolve(AllTypesConfig.class.getSimpleName() + ext);
    configFile.toFile().getParentFile().mkdirs();

    try (PrintStream out = new PrintStream(configFile.toFile(), "UTF-8")) {
      out.println("strValue=Привет УАУ мир!!!");
      out.println("intValue=78324");
      out.println("longValue=6753876840009");
      out.println("boolValue1=false");
      out.println("boolValue2=true");
      out.println("doubleValue=5426.76548");
    }

    AllTypesConfigFactory factory = new AllTypesConfigFactory(baseDir, ext);

    //
    //
    AllTypesConfig config = factory.createAllTypesConfig();
    //
    //

    assertThat(config.strValue()).isEqualTo("Привет УАУ мир!!!");
    assertThat(config.intValue()).isEqualTo(78324);
    assertThat(config.longValue()).isEqualTo(6753876840009L);
    assertThat(config.boolValue1()).isEqualTo(false);
    assertThat(config.boolValue2()).isEqualTo(true);
    assertThat(config.doubleValue()).isEqualTo(5426.76548);

  }

  @ConfigFileName("AnotherFileName")
  public interface ConfigWithAnotherFile {
    @SuppressWarnings("UnusedReturnValue")
    String someParam();
  }

  public static class ConfigWithAnotherFileFab extends FileConfigFactory {
    private final Path baseDir;

    public ConfigWithAnotherFileFab(Path baseDir) {
      this.baseDir = baseDir;
    }

    @Override
    protected String getConfigFileExt() {
      return ".txt";
    }

    @Override
    protected Path getBaseDir() {
      return baseDir;
    }

    public ConfigWithAnotherFile createConfigWithAnotherFile() {
      return createConfig(ConfigWithAnotherFile.class);
    }
  }

  @Test
  public void check_ConfigFileName() {

    String dir = getClass().getSimpleName() + "/" + RND.str(10);

    ConfigWithAnotherFileFab fab  = new ConfigWithAnotherFileFab(Paths.get("build").resolve(dir));
    ConfigWithAnotherFile    conf = fab.createConfigWithAnotherFile();
    conf.someParam();


    File f = new File("build/" + dir + "/AnotherFileName.txt");
    assertThat(f).exists();

  }
}
