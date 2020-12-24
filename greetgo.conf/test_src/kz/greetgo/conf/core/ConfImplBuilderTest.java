package kz.greetgo.conf.core;

import kz.greetgo.conf.hot.CannotConvertToType;
import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("BusyWait")
public class ConfImplBuilderTest {

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

    assertThat(1);
  }

}
