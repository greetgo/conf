package kz.greetgo.conf.hot;

import kz.greetgo.conf.ConfUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

public class TestCloudHotConfigFromFile extends CloudFileConfigFactory {

  private final Path baseDir;
  private final String configFileExt;

  public TestCloudHotConfigFromFile(Path baseDir, String configFileExt) {
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

  @Override
  protected String getServerBaseUrl() {
    return "";
  }

  @Override
  protected String getApplication() {
    return "";
  }

  public HotConfig1 createConfig1() {
    return createConfig(HotConfig1.class);
  }

  public HotConfig2 createConfig2() {
    return createConfig(HotConfig2.class);
  }

  private final ConfigStorage cloudConfigStorage = new ConfigStorage() {

    @Override
    public String loadCloudConfigContent() {
      return null;
    }

    @Override
    public String loadConfigContent(String configLocation) {
      return ConfUtil.readFile(configStorageFile(configLocation));
    }

    @Override
    public boolean isConfigContentExists(String configLocation) {
      return configStorageFile(configLocation).exists();
    }

    @Override
    public void saveConfigContent(String configLocation, String configContent) {
      File file = configStorageFile(configLocation);
      //noinspection ResultOfMethodCallIgnored
      file.getParentFile().mkdirs();
      ConfUtil.writeFile(file, configContent);
    }

    @Override
    public Date getLastChangedAt(String configLocation) {
      File file = configStorageFile(configLocation);
      long lastModified = file.lastModified();
      return lastModified == 0L ? null : new Date(lastModified);
    }

  };

  @Override
  protected ConfigStorage getConfigStorage() {
    return cloudConfigStorage;
  }
}
