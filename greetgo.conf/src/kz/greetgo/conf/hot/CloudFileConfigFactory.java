package kz.greetgo.conf.hot;


import kz.greetgo.conf.ConfUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

/**
 * Creates proxy-instances to access to config data with method {@link #createConfig(Class)}
 * <p/>
 * <p>
 * Cloud server url for config files returning with method {@link #getServerBaseUrl()}
 * </p>
 * <p>
 * <p/>
 * <p>
 * Default config file located in folder of running server returning with method {@link #getBaseDir()}.
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
public abstract class CloudFileConfigFactory extends AbstractConfigFactory {

  /**
   * Returns folder path where config files will be created
   *
   * @return folder path where config files will be created
   */
  protected abstract Path getBaseDir();

  /**
   * Returns server path where config files will be stored
   *
   * @return server path where config files will be stored
   */
  protected abstract String getServerBaseUrl();

  /**
   * Returns application property file name
   *
   * @return application property file name
   */
  protected abstract String getApplication();

  /**
   * Returns an active profile (or comma-separated list of properties)
   *
   * @return an active profile (or comma-separated list of properties)
   */
  protected String getProfile() {
    return null;
  }

  /**
   * Returns an optional git label (defaults to master.)
   *
   * @return an optional git label (defaults to master.)
   */
  protected String getLabel() {
    return "master";
  };


  @Override
  protected <T> String configLocationFor(Class<T> configInterface) {
    return configInterface.getSimpleName() + getConfigFileExt();
  }

  protected String nvl(String value) {
    return Objects.isNull(value)?"":"/"+value;
  }

  protected File configStorageFile(String configLocation) {
    return getBaseDir().resolve(configLocation).toFile();
  }

  public File storageFileFor(Class<?> configInterface) {
    return configStorageFile(configLocationFor(configInterface));
  }

  protected String getConfigFileExt() {
    return ".hotconfig";
  }

  @Override
  protected final <T> boolean isCloud() {
    return true;
  }

  private final ConfigStorage cloudConfigStorage = new ConfigStorage() {

    @Override
    public String loadCloudConfigContent() {
      return ConfUtil.readCloudFileContent(getServerBaseUrl() + "/" + getApplication() + nvl(getProfile()) + nvl(getLabel()) );
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
