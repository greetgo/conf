package kz.greetgo.conf.hot;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import kz.greetgo.conf.ConfUtil;

import java.io.File;

/**
 * Creates proxy-instances to access to config data with method {@link #createConfig(Class)}
 * <p/>
 * <p>
 * Config file located in folder returing with method {@link #getBaseDir()}.
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
@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
public abstract class HotConfigFactory extends AbstractConfigFactory {
  /**
   * Returns folder path where config files will create
   *
   * @return folder path where config files will create
   */
  protected abstract String getBaseDir();

  @Override
  protected <T> String configLocationFor(Class<T> configInterface) {
    return configInterface.getSimpleName() + getConfigFileExt();
  }

  private File configStorageFile(String configLocation) {
    return new File(getBaseDir() + File.separator + configLocation);
  }

  public File storageFileFor(Class<?> configInterface) {
    return configStorageFile(configLocationFor(configInterface));
  }

  protected String getConfigFileExt() {
    return ".hotconfig";
  }

  private final ConfigStorage configStorage = new ConfigStorage() {
    @Override
    public String loadConfigContent(String configLocation) throws Exception {
      return ConfUtil.readFile(configStorageFile(configLocation));
    }

    @Override
    public boolean isConfigContentExists(String configLocation) throws Exception {
      return configStorageFile(configLocation).exists();
    }

    @Override
    public void saveConfigContent(String configLocation, String configContent) throws Exception {
      File file = configStorageFile(configLocation);
      file.getParentFile().mkdirs();
      ConfUtil.writeFile(file, configContent);
    }
  };

  @Override
  protected ConfigStorage getConfigStorage() {
    return configStorage;
  }
}
