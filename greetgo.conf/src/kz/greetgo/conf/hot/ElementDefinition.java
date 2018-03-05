package kz.greetgo.conf.hot;

import kz.greetgo.conf.type_manager.TypeManager;
import kz.greetgo.conf.type_manager.TypeManagerCache;

/**
 * Definition of config element
 */
public class ElementDefinition {
  /**
   * Config element name
   */
  public final String name;

  /**
   * Type manager for operation with type
   */
  public final TypeManager typeManager;

  /**
   * Config element default value
   */
  final Object defaultValue;

  /**
   * Description of config element
   */
  public final String description;

  /**
   * Mark that element is list
   */
  public final boolean isList;

  private ElementDefinition(String name, TypeManager typeManager, Object defaultValue, String description, boolean isList) {
    if (defaultValue instanceof TypeManager) throw new IllegalArgumentException("defaultValue cannot be TypeManager");
    this.name = name;
    this.typeManager = typeManager;
    this.defaultValue = defaultValue;
    this.description = description;
    this.isList = isList;
  }

  public static ElementDefinition newOne(String name, TypeManager typeManager, Object defaultValue, String description) {
    return new ElementDefinition(name, typeManager, defaultValue, description, false);
  }

  public static ElementDefinition newOne(String name, Class<?> type, Object defaultValue, String description) {
    return new ElementDefinition(name, TypeManagerCache.getOrCreate(type), defaultValue, description, false);
  }

  public Object newDefaultValue() {
    return typeManager.newDefaultValue(defaultValue);
  }

  public LineStructure createLineStructure() {
    return typeManager.createLineStructure(name, defaultValue, description, isList);
  }

  public static ElementDefinition newList(String name, Class<?> type, Object defaultValue, String description) {
    return new ElementDefinition(name, TypeManagerCache.getOrCreate(type), defaultValue, description, true);
  }

  public static ElementDefinition newList(String name, TypeManager typeManager, Object defaultValue, String description) {
    return new ElementDefinition(name, typeManager, defaultValue, description, true);
  }
}
