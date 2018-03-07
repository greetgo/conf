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
   * Define default list size. If null then is not list
   */
  public final Integer defaultListSize;

  private ElementDefinition(String name, TypeManager typeManager, Object defaultValue, String description, Integer defaultListSize) {
    if (defaultValue instanceof TypeManager) throw new IllegalArgumentException("defaultValue cannot be TypeManager");
    this.name = name;
    this.typeManager = typeManager;
    this.defaultValue = defaultValue;
    this.description = description;
    this.defaultListSize = defaultListSize;
  }

  public static ElementDefinition newOne(String name, Class<?> type, Object defaultValue, String description) {
    return new ElementDefinition(name, TypeManagerCache.getOrCreate(type), defaultValue, description, null);
  }

  public Object newDefaultValue() {
    return typeManager.newDefaultValue(defaultValue);
  }

  public LineStructure createLineStructure() {
    return typeManager.createLineStructure(name, defaultValue, description, defaultListSize);
  }

  public static ElementDefinition newList(String name, Class<?> type, Object defaultValue, String description) {
    return new ElementDefinition(name, TypeManagerCache.getOrCreate(type), defaultValue, description, 1);
  }

  public static ElementDefinition create(String name,
                                         TypeManager typeManager,
                                         Object defaultValue,
                                         String description,
                                         Integer defaultListSize) {

    return new ElementDefinition(name, typeManager, defaultValue, description, defaultListSize);

  }

  public static ElementDefinition create(String name,
                                         Class<?> type,
                                         Object defaultValue,
                                         String description,
                                         Integer defaultListSize) {

    return new ElementDefinition(name, TypeManagerCache.getOrCreate(type), defaultValue, description, defaultListSize);

  }
}
