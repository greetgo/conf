package kz.greetgo.conf.hot;

/**
 * Hot config defined by {@link HotConfigDefinition}
 */
public interface HotConfig {

  /**
   * Takes value of config element
   *
   * @param elementName config element name
   * @return config element value
   */
  <T> T getElementValue(String elementName);

  /**
   * Check existence of config element
   *
   * @param elementName config element name
   * @return <code>true</code> - element exists, otherwise - does not
   */
  boolean isElementExists(String elementName);

  /**
   * Returns config location
   *
   * @return config location
   */
  String location();

  /**
   * Returns config interface class
   *
   * @return config interface class
   */
  Class<?> configInterface();

}
