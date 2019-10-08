package kz.greetgo.conf.hot;

import kz.greetgo.conf.ConfUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

public class TestCloudHotConfigFab extends CloudFileConfigFactory {

  private final Path baseDir;
  private final String configFileExt;

  public TestCloudHotConfigFab(Path baseDir, String configFileExt) {
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
      return "{\"name\":\"test-config\",\"profiles\":[\"master\"],\"label\":null,\"version\":\"b37442a398d024b10fcccb071aefbb2ef9b5db95\",\"state\":null,\"propertySources\":[{\"name\":\"http://gitlab.greetgo/aix/aix_cloud_config.git/test-config.properties\"," +
          "\"source\":{" +
          "\"HotConfig1.strExampleValue\":\"check the value\"," +
          "\"HotConfig1.intExampleValue\": 1," +
          "\"HotConfig1.boolExampleValue\": false," +
          "\"HotConfig2.asd\":\"check asd the value\"," +
          "\"HotConfig2.intAsd\": 457" +
          "}}]}\n";
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
