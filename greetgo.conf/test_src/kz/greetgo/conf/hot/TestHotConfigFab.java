package kz.greetgo.conf.hot;

import java.nio.file.Path;

public class TestHotConfigFab extends FileConfigFactory {

  private final Path baseDir;
  private final String configFileExt;

  public TestHotConfigFab(Path baseDir, String configFileExt) {
    this.baseDir = baseDir;
    this.configFileExt = configFileExt;
  }

  @Override
  protected String getConfigFileExt() {
    return configFileExt;
  }

  @Override
  protected Path getBaseDir() {
    return baseDir;
  }

  public HotConfig1 createConfig1() {
    return createConfig(HotConfig1.class);
  }

  public HotConfig2 createConfig2() {
    return createConfig(HotConfig2.class);
  }
}
