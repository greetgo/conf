package kz.greetgo.conf.core;

import kz.greetgo.conf.error.CannotConvertToType;
import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;
import kz.greetgo.conf.hot.ForcibleInit;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("BusyWait")
public class ConfImplBuilderIntegrationTest {

  @Description("Description of TestConfig")
  interface TestConfig {

    @Description("Description of param paramStr")
    @DefaultStrValue("default wow")
    String paramStr();

    @Description("Description of param paramInt")
    @DefaultIntValue(444011)
    int paramInt();

    @Description("Порт доступа ко всему")
    @DefaultIntValue(8080)
    int port();

  }

  @Test
  public void build() throws Exception {
    Path dir         = Paths.get("build").resolve(getClass().getSimpleName());
    File workingFile = dir.resolve("kill-me-to-stop-working").toFile();
    File lockFile    = dir.resolve("lockFile").toFile();
    File lockFileT   = dir.resolve("lockFile.killItToLock").toFile();

    if (!lockFile.exists()) {
      lockFileT.getParentFile().mkdirs();
      lockFileT.createNewFile();
    }

    File       file       = dir.resolve("test-config.txt").toFile();
    ConfAccess confAccess = new ConfAccessFile(file, new ConfAccessStdSerializer());

    ConfImplBuilder<TestConfig> builder = ConfImplBuilder
                                            .confImplBuilder(TestConfig.class, confAccess)
                                            .changeCheckTimeoutMs(1000);

    //
    //
    TestConfig testConfig = builder.build();
    //
    //

    workingFile.getParentFile().mkdirs();
    workingFile.createNewFile();

    while (lockFile.exists() && workingFile.exists()) {
      try {

        System.out.println("Ls05Lc5bjk :: testConfig.paramStr = " + testConfig.paramStr());
        System.out.println("Ls05Lc5bjk :: testConfig.paramInt = " + testConfig.paramInt());
        System.out.println("Ls05Lc5bjk :: testConfig.port     = " + testConfig.port());

      } catch (CannotConvertToType e) {
        e.printStackTrace();
      }

      Thread.sleep(1500);
    }

  }

  @ForcibleInit
  interface TestConfig2 {
    @DefaultStrValue("It is OK")
    String statusQuo();
  }

  @Test
  public void build__ForcibleInit() throws Exception {
    Path dir         = Paths.get("build").resolve(getClass().getSimpleName());
    File workingFile = dir.resolve("kill-me-to-stop-working").toFile();
    File lockFile    = dir.resolve("lockFile").toFile();
    File lockFileT   = dir.resolve("lockFile.killItToLock").toFile();

    if (!lockFile.exists()) {
      lockFileT.getParentFile().mkdirs();
      lockFileT.createNewFile();
    }

    File       doNotRead   = dir.resolve("do-not-read").toFile();
    File       file        = dir.resolve("test-config.txt").toFile();
    File       file2       = dir.resolve("test-config-ForcibleInit.txt").toFile();
    ConfAccess confAccess  = new ConfAccessFile(file, new ConfAccessStdSerializer());
    ConfAccess confAccess2 = new ConfAccessFile(file2, new ConfAccessStdSerializer());

    ConfImplBuilder<TestConfig> builder = ConfImplBuilder
                                            .confImplBuilder(TestConfig.class, confAccess)
                                            .changeCheckTimeoutMs(1000);
    ConfImplBuilder<TestConfig2> builder2 = ConfImplBuilder
                                              .confImplBuilder(TestConfig2.class, confAccess2)
                                              .changeCheckTimeoutMs(1000);

    //
    //
    TestConfig  testConfig  = builder.build();
    TestConfig2 testConfig2 = builder2.build();
    //
    //

    workingFile.getParentFile().mkdirs();
    workingFile.createNewFile();
    doNotRead.getParentFile().mkdirs();
    doNotRead.createNewFile();

    System.out.println("FZr8D2Y89V :: Started");

    while (lockFile.exists() && workingFile.exists()) {
      try {

        if (!doNotRead.exists()) {
          System.out.println("gACby3JD9T :: testConfig2.statusQuo = " + testConfig2.statusQuo());
          System.out.println("gACby3JD9T :: testConfig.statusQuo  = " + testConfig.port());
          System.out.println("gACby3JD9T :: testConfig.paramStr   = " + testConfig.paramStr());
          System.out.println("gACby3JD9T :: testConfig.paramInt   = " + testConfig.paramInt());
        }

      } catch (CannotConvertToType e) {
        e.printStackTrace();
      }

      Thread.sleep(1500);
    }

    System.out.println("1VD20f89EP :: Finished");
  }

}
