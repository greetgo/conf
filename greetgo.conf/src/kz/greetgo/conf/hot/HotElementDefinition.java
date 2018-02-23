package kz.greetgo.conf.hot;

/**
 * Definition of config element
 */
public class HotElementDefinition {
  /**
   * Config element name
   */
  public String name;

  /**
   * Config element type
   */
  public Class<?> type;

  /**
   * Config element default value
   */
  public Object defaultValue;

  /**
   * Description of config element
   */
  public String description;

  public HotElementDefinition() {
  }

  public HotElementDefinition(String name, Class<?> type, Object defaultValue, String description) {
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
    this.description = description;
  }
}
