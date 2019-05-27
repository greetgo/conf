package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.hot.CannotWorkWithType;

import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

public class TypeManagerCache {

  private static final ConcurrentHashMap<Class<?>, TypeManager> cachedValues = new ConcurrentHashMap<>();

  public static TypeManager getOrCreate(Class<?> type) {
    if (TypeManagerPrimitive.forMe(type)) {
      return cachedValues.computeIfAbsent(type, TypeManagerPrimitive::new);
    }

    //noinspection PointlessBooleanExpression
    if (false

      || type.isInterface()
      || Modifier.isAbstract(type.getModifiers())
      || type.isAnnotation()
      || type.isArray()
      || type.isEnum()

      ) throw new CannotWorkWithType(type);


    {
      return cachedValues.computeIfAbsent(type, TypeManagerClass::new);
    }
  }

}
