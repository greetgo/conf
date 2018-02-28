package kz.greetgo.conf.type_manager;

import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

public class TypeManagerCache {

  private static final ConcurrentHashMap<Class<?>, TypeManager> cachedValues = new ConcurrentHashMap<>();

  public static <T> TypeManager<T> getOrCreate(Class<T> type) {
    if (type.isInterface()
      || Modifier.isAbstract(type.getModifiers())
      || type.isAnnotation()
      || type.isArray()
      || type.isEnum()
      || type.isPrimitive()) return null;

    //noinspection unchecked
    return cachedValues.computeIfAbsent(type, TypeManagerImpl::new);
  }

}
