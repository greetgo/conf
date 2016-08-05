package kz.greetgo.conf.hot;

/**
 * Defines hot config infrastructure
 */
public interface HotConfigDefinition extends Iterable<HotElementDefinition> {
  /**
   * Hot config location
   *
   * @return hot config location
   */
  String location();

  /**
   * Description of hot config
   *
   * @return description of hot config, may be null
   */
  String description();

  /**
   * Determine config element count
   *
   * @return config element count
   */
  int elementCount();

  /**
   * Returns config element
   *
   * @param index index of config element
   * @return config element definition
   */
  HotElementDefinition element(int index);
}
