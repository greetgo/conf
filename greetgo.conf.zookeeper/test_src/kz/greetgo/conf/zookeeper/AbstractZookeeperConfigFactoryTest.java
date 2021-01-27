package kz.greetgo.conf.zookeeper;

import kz.greetgo.conf.zookeeper.test.TestConfig;
import kz.greetgo.conf.zookeeper.test.VoidHandle;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractZookeeperConfigFactoryTest {

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

    String baseDir = "test/wow/tangent_" + RND.strEng(15);

    AbstractZookeeperConfigFactory factory = new AbstractZookeeperConfigFactory() {
      @Override
      protected String zooConnectionString() {
        return "localhost:51078";
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
}
