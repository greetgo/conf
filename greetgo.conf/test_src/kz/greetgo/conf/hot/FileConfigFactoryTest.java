package kz.greetgo.conf.hot;

import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintStream;
import java.util.Random;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileConfigFactoryTest {
  @Test
  public void createDefault() {
    {
      File f = new File("build/asd/HotConfig1.hot");
      if (f.exists()) f.delete();
    }

    TestHotConfigFab fab = new TestHotConfigFab("build/asd", ".hot");
    HotConfig1 conf = fab.createConfig1();

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

      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 711");
      out.close();
    }

    TestHotConfigFab fab = new TestHotConfigFab("build/asd", ".hot");
    HotConfig1 conf = fab.createConfig1();

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

      try (PrintStream out = new PrintStream(f, "UTF-8")) {
        out.println("intExampleValue = 7111");
        out.println("intExampleValue2 = 444");
        out.println("boolExampleValue = 0");
        out.println("strExampleValue = pa ra be lum");
      }
    }

    TestHotConfigFab fab = new TestHotConfigFab("build/asd", ".hot");
    HotConfig1 conf = fab.createConfig1();

    assertThat(conf.intExampleValue()).isEqualTo(7111);
    assertThat(conf.intExampleValue2()).isEqualTo(444);
    assertThat(conf.boolExampleValue()).isEqualTo(false);
    assertThat(conf.strExampleValue()).isEqualTo("pa ra be lum");
  }

  @Test
  public void reset() throws Exception {

    File f = new File("build/asd/HotConfig1.hot");
    if (f.exists()) {
      f.delete();
    }

    {
      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 7111");
      out.println("intExampleValue2 = 444");
      out.println("boolExampleValue = 0");
      out.println("strExampleValue = status");
      out.close();
    }

    TestHotConfigFab fab = new TestHotConfigFab("build/asd", ".hot");
    HotConfig1 conf = fab.createConfig1();

    assertThat(conf.intExampleValue()).isEqualTo(7111);
    assertThat(conf.intExampleValue2()).isEqualTo(444);
    assertThat(conf.boolExampleValue()).isEqualTo(false);
    assertThat(conf.strExampleValue()).isEqualTo("status");

    {
      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 999");
      out.println("intExampleValue2 = 111");
      out.println("boolExampleValue = 1");
      out.println("strExampleValue = quantum");
      out.close();
    }

    fab.resetAll();

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


    final String baseDir = "build/parentingOfHotConfigs_baseDir/" + rnd.nextInt(10_000_000);

    FileConfigFactory fileConfigFactory = new FileConfigFactory() {
      @Override
      protected String getBaseDir() {
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
    final String baseDir = "build/testingMethod_toString_inConfig/" + rnd.nextInt(10_000_000);

    FileConfigFactory fileConfigFactory = new FileConfigFactory() {
      @Override
      protected String getBaseDir() {
        return baseDir;
      }
    };

    ConcreteHotConfig config = fileConfigFactory.createConfig(ConcreteHotConfig.class);


    assertThat(config.toString()).isNotNull();
    assertThat(config.toString()).isNotEqualTo("null");
    assertThat(config.toString()).contains(ConcreteHotConfig.class.getName());

  }
}
