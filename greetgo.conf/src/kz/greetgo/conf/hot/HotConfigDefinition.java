package kz.greetgo.conf.hot;

import java.util.List;

/**
 * Defines hot config infrastructure
 */
public interface HotConfigDefinition {
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
   * Returns config element definition list
   *
   * @return config element definition list
   */
  List<ElementDefinition> elementList();
}
