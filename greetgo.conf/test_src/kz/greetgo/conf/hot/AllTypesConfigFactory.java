package kz.greetgo.conf.hot;

import java.nio.file.Path;

public class AllTypesConfigFactory extends FileConfigFactory {
  private final Path   baseDir;
  private final String configFileExt;

  public AllTypesConfigFactory(Path baseDir, String configFileExt) {
    this.baseDir       = baseDir;
    this.configFileExt = configFileExt;
  }

  @Override
  protected Path getBaseDir() {
    return baseDir;
  }

  @Override
  public String getConfigFileExt() {
    return configFileExt;
  }

  public AllTypesConfig createAllTypesConfig() {
    return createConfig(AllTypesConfig.class);
  }
}
