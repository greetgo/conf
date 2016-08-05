package kz.greetgo.conf.hot;

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

}
