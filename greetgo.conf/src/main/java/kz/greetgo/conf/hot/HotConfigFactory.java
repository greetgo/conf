package kz.greetgo.conf.hot;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
public abstract class HotConfigFactory extends AbstractConfigFactoryOld {
  /**
   * Returns folder path where config files will create
   *
   * @return folder path where config files will create
   */
  protected abstract String getBaseDir();

  private File configStorageFile(Class<?> configInterface) {
    return new File(getBaseDir() + File.separator + configInterface.getSimpleName() + getConfigFileExt());
  }

  protected String getConfigFileExt() {
    return ".hotconfig";
  }

  protected InputStream getConfigStorageInputStream(Class<?> configInterface) throws Exception {
    return configStorageFile(configInterface).toURI().toURL().openStream();
  }

  protected boolean isConfigStorageExists(Class<?> configInterface) {
    return configStorageFile(configInterface).exists();
  }

  protected OutputStream getConfigStorageAppendingOutputStream(Class<?> configInterface) throws Exception {
    File file = configStorageFile(configInterface);
    file.getParentFile().mkdirs();
    return new FileOutputStream(file, true);
  }
}
