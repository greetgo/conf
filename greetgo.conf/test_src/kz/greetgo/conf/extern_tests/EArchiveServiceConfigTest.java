package kz.greetgo.conf.extern_tests;

import kz.greetgo.conf.RND;
import kz.greetgo.conf.hot.FileConfigFactory;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class EArchiveServiceConfigTest {

  @Test
  public void readConfig() throws Exception {

    Path parent = Paths
      .get("build")
      .resolve("EArchiveServiceConfigTest")
      .resolve(RND.str(10))
      .resolve("parent");
    Path path = parent
      .resolve("EArchiveServiceConfig.hotconfig");

    //noinspection ResultOfMethodCallIgnored
    path.toFile().getParentFile().mkdirs();
    try (InputStream resourceAsStream = getClass().getResourceAsStream("EArchiveServiceConfig.hotconfig")) {
      try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
        byte[] buffer = new byte[1024];
        while (true) {
          int count = resourceAsStream.read(buffer);
          if (count < 0) {
            break;
          }
          outputStream.write(buffer, 0, count);
        }
      }
    }

    class Factory extends FileConfigFactory {
      @Override
      protected Path getBaseDir() {
        return parent;
      }

      EArchiveServiceConfig create() {
        return createConfig(EArchiveServiceConfig.class);
      }
    }

    Factory factory = new Factory();

    EArchiveServiceConfig config = factory.create();

    assertThat(config.url()).isEqualTo("http://esb-tibco.hq.bc:27745/EArchive_DocsByRef_Get_Ws111111");

  }
}
