package kz.greetgo.conf.hot;

import kz.greetgo.conf.ConfUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

/**
 * Creates proxy-instances to access to config data with method {@link #createConfig(Class)}
 * <p/>
 * <p>
 * Config file located in folder returning with method {@link #getBaseDir()}.
 * </p>
 * <p>
 * Config file name is config interface name
 * </p>
 * <p>
 * Config file extension defined with method  {@link #getConfigFileExt()}
 * </p>
 *
 * @author pompei
 */
public abstract class FileConfigFactory extends AbstractConfigFactory {

  /**
   * Returns folder path where config files will be created
   *
   * @return folder path where config files will be created
   */
  protected abstract Path getBaseDir();

  @Override
  protected <T> String configLocationFor(Class<T> configInterface) {
    return configInterface.getSimpleName() + getConfigFileExt();
  }

  @Override
  protected <T> boolean isCloud() {
    return false;
  }

  private File configStorageFile(String configLocation) {
    return getBaseDir().resolve(configLocation).toFile();
  }

  public File storageFileFor(Class<?> configInterface) {
    return configStorageFile(configLocationFor(configInterface));
  }

  protected String getConfigFileExt() {
    return ".hotconfig";
  }

  private final ConfigStorage configStorage = new ConfigStorage() {

    @Override
    public Map<String, Object> loadCloudConfigContent() throws Exception {
      throw new UnsupportedOperationException("File doesn't have cloud content");
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
    return configStorage;
  }

}
