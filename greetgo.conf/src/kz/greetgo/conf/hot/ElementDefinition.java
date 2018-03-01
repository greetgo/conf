package kz.greetgo.conf.hot;

import kz.greetgo.conf.type_manager.TypeManager;
import kz.greetgo.conf.type_manager.TypeManagerCache;

import java.util.List;

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

  public ElementDefinition(String name, TypeManager typeManager, Object defaultValue, String description) {
    if (defaultValue instanceof TypeManager) throw new IllegalArgumentException("defaultValue cannot be TypeManager");
    this.name = name;
    this.typeManager = typeManager;
    this.defaultValue = defaultValue;
    this.description = description;
  }

  public ElementDefinition(String name, Class<?> type, Object defaultValue, String description) {
    this(name, TypeManagerCache.getOrCreate(type), defaultValue, description);
  }

  public Object newDefaultValue() {
    return typeManager.newDefaultValue(defaultValue);
  }

  public List<LineHibernate> createLineHibernateList() {
    return typeManager.createLineHibernateList(name, defaultValue, description);
  }
}
