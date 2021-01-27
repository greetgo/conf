package kz.greetgo.conf.zookeeper;

import kz.greetgo.conf.zookeeper.test.TestConfig;
import kz.greetgo.conf.zookeeper.test.VoidHandle;
import kz.greetgo.conf.zookeeper.test.ZooOperations;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractZookeeperConfigFactoryTest {

  private final ZooOperations zoo = new ZooOperations("localhost:51078");

  private final List<VoidHandle> closers = new ArrayList<>();

  @AfterMethod
  public void closeAll() throws Exception {
    List<VoidHandle> list = new ArrayList<>(closers);
    closers.clear();
    for (VoidHandle h : list) {
      h.handle();
    }
  }

  @Test
  public void readDefaultValue() {
    zoo.skipTestIfNoZoo();

    String baseDir = "tests/readDefaultValue/tangent_" + RND.strEng(15);

    AbstractZookeeperConfigFactory factory = new AbstractZookeeperConfigFactory() {
      @Override
      protected String zooConnectionString() {
        return zoo.connectionStr();
      }

      @Override
      protected String baseDir() {
        return baseDir;
      }
    };
    closers.add(factory::close);

    TestConfig config = factory.createConfig(TestConfig.class);

    assertThat(config.strParam()).isEqualTo("82r8O2766e");
    assertThat(config.longParam()).isEqualTo(47836125876L);
  }

  @Test
  public void readStoredValue() {
    zoo.skipTestIfNoZoo();

    String baseDir = "tests/readStoredValue/tangent_" + RND.strEng(15);

    String strParamValue = RND.str(10);

    AbstractZookeeperConfigFactory factory = new AbstractZookeeperConfigFactory() {
      @Override
      protected String zooConnectionString() {
        return zoo.connectionStr();
      }

      @Override
      protected String baseDir() {
        return baseDir;
      }
    };
    closers.add(factory::close);

    TestConfig config = factory.createConfig(TestConfig.class);

    String filePath = "/" + baseDir + "/TestConfig.hotconfig";

    String beginContent = zoo.readFile(filePath);
    assertThat(beginContent).isNull();

    zoo.writeFile(filePath, "strParam=" + strParamValue);

    assertThat(config.strParam()).isEqualTo(strParamValue);
    assertThat(config.longParam()).isEqualTo(47836125876L);
  }
}
