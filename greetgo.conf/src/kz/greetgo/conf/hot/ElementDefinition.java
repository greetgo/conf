package kz.greetgo.conf.hot;

import kz.greetgo.conf.type_manager.TypeManager;

/**
 * Definition of config element
 */
public class ElementDefinition {
  /**
   * Config element name
   */
  public final String name;

  /**
   * Config element type
   */
  public final Class<?> type;

  /**
   * Config element default value
   */
  final Object defaultValue;

  /**
   * Description of config element
   */
  public final String description;

  public ElementDefinition(String name, Class<?> type, Object defaultValue, String description) {
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
    this.description = description;
  }

  public Object newDefaultValue() {
    if (defaultValue instanceof TypeManager) {
      return ((TypeManager) defaultValue).newDefaultValue();
    }
    return defaultValue;
  }
}