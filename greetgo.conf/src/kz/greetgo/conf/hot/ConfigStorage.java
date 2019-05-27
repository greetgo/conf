package kz.greetgo.conf.hot;

import java.util.Date;

/**
 * Config storage interface. Is is used by library to store config data. You can redefine it to store config any where.
 * It is used in {@link AbstractConfigFactory} to access to config storing data.
 */
public interface ConfigStorage {

  /**
   * Loads and returns config content
   *
   * @param configLocation config location
   * @return config content
   */
  String loadConfigContent(String configLocation) throws Exception;

  /**
   * Checks config content exists
   *
   * @param configLocation config location
   * @return check status
   */
  boolean isConfigContentExists(String configLocation) throws Exception;

  /**
   * Saves config content
   *
   * @param configLocation config location
   * @param configContent  content of config
   */
  void saveConfigContent(String configLocation, String configContent) throws Exception;

  /**
   * Returns last changed timestamp
   *
   * @param configLocation config location
   * @return last changed timestamp, or <code>null</code>, if file is absent
   */
  Date getLastChangedAt(String configLocation) throws Exception;

}
