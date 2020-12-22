package kz.greetgo.conf.core;

import kz.greetgo.conf.core.fields.ConfIgnore;
import kz.greetgo.conf.core.fields.FieldAccess;
import kz.greetgo.conf.core.fields.FieldParserDefault;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;
import static kz.greetgo.conf.ConfUtil.convertToType;
import static kz.greetgo.conf.ConfUtil.convertibleTypeList;
import static kz.greetgo.conf.ConfUtil.isConvertingType;

public class ConfImplToCallback<T> implements InvocationHandler {
  private final Class<T> interfaceClass;
  private final ConfCallback confCallback;
  private final T impl;

  public ConfImplToCallback(Class<T> interfaceClass, ConfCallback confCallback) {
    this.interfaceClass = interfaceClass;
    this.confCallback = confCallback;
    //noinspection unchecked
    impl = (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{interfaceClass}, this);
  }

  public T impl() {
    return impl;
  }

  private final ConcurrentHashMap<String, List<ConfImplToCallback<?>>> subCallbackLists = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, ConfImplToCallback<?>> subCallbacks = new ConcurrentHashMap<>();

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String methodName = method.getName();
    Class<?> returnType = method.getReturnType();

    if ("equals".equals(methodName)
      && method.getParameterCount() == 1
      && Object.class.equals(method.getParameterTypes()[0])
    ) {
      return equals(args[0]);
    }

    if (method.getParameterCount() != 0) {
      throw new Exception("0TloKZKQjG :: method = " + method);
    }

    if ("hashCode".equals(methodName)) {
      return hashCode();
    }

    if (isConvertingType(returnType)) {
      String strValue = confCallback.readParam(methodName);
      return convertToType(strValue, returnType);
    }

    if (List.class.isAssignableFrom(returnType)) {

      Class<?> arrArg = extractFirstArgumentClass(method.getAnnotatedReturnType().getType());

      if (isConvertingType(arrArg)) {
        int count = confCallback.readParamSize(methodName);
        //noinspection rawtypes
        ArrayList ret = new ArrayList();
        for (int i = 0; i < count; i++) {
          //noinspection unchecked
          ret.add(convertToType(confCallback.readParam(methodName + '.' + i), arrArg));
        }
        return ret;
      }

      if (arrArg.isInterface()) {
        int size = confCallback.readParamSize(methodName);

        final List<ConfImplToCallback<?>> curList = subCallbackLists.get(methodName);
        if (curList == null) {
          List<ConfImplToCallback<?>> ret = new ArrayList<>(size);
          for (int i = 0; i < size; i++) {
            ConfCallbackPrefix callbackPrefix = new ConfCallbackPrefix(methodName + '.' + i + '.', confCallback);
            ret.add(new ConfImplToCallback<>(arrArg, callbackPrefix));
          }
          subCallbackLists.put(methodName, ret);
          return ret.stream().map(ConfImplToCallback::impl).collect(toList());
        }
        while (size < curList.size()) {
          curList.remove(curList.size() - 1);
        }
        if (curList.size() == size) {
          return curList.stream().map(ConfImplToCallback::impl).collect(toList());
        }
        // assert curList.size() < size;
        for (int i = curList.size(); i < size; i++) {
          ConfCallbackPrefix callbackPrefix = new ConfCallbackPrefix(methodName + '.' + i + '.', confCallback);
          curList.add(new ConfImplToCallback<>(arrArg, callbackPrefix));
        }
        return curList.stream().map(ConfImplToCallback::impl).collect(toList());
      }

      throw new Exception("Yxz55c8nKQ :: Config parameter " + methodName + " in " + interfaceClass
        + " has incorrect type. Use an interface instead of a class");
    }

    if (returnType.isInterface()) {
      {
        ConfImplToCallback<?> ret = subCallbacks.get(method.getName());
        if (ret != null) return ret.impl();
      }
      {
        ConfCallbackPrefix callbackPrefix = new ConfCallbackPrefix(method.getName() + '.', confCallback);
        ConfImplToCallback<?> ret = new ConfImplToCallback<>(method.getReturnType(), callbackPrefix);
        subCallbacks.put(method.getName(), ret);
        return ret.impl();
      }
    }

    {
      Constructor<?> defaultConstructor = returnType.getConstructor();

      Object ret = defaultConstructor.newInstance();

      for (FieldAccess field : FieldParserDefault.instance.parse(returnType).values()) {

        Class<?> fieldType = field.type();

        if (!isConvertingType(fieldType)) {
          throw new RuntimeException("va8zf1h5fZ :: Cannot write to field with type "
            + fieldType + ". Remove, hide, or mark with annotation @"
            + ConfIgnore.class.getSimpleName()
            + " this field. You can use the following field types: " + convertibleTypeList());
        }

        String strValue = confCallback.readParam(methodName + '.' + field.name());

        field.setValue(ret, convertToType(strValue, fieldType));

      }

      return ret;
    }
  }

  private static Class<?> extractFirstArgumentClass(Type type) {
    if (!(type instanceof ParameterizedType)) {
      throw new RuntimeException("Tg9D1M736h :: cannot extractFirstArgumentClass from " + type);
    }

    ParameterizedType p = (ParameterizedType) type;
    Type[] actualTypeArguments = p.getActualTypeArguments();
    if (actualTypeArguments.length == 0) {
      throw new RuntimeException("aB98tR0cdq :: cannot extractFirstArgumentClass from " + type);
    }

    Type typeArgument = actualTypeArguments[0];

    if (typeArgument instanceof Class) {
      return (Class<?>) typeArgument;
    }

    if (typeArgument instanceof ParameterizedType) {
      ParameterizedType p2 = (ParameterizedType) typeArgument;
      return (Class<?>) p2.getRawType();
    }

    throw new RuntimeException("spE6Q3TS9n :: cannot extractFirstArgumentClass from " + type);
  }

  public ConfContent defaultContent() {
    // этот метод должен работать в ленивом режиме
    throw new RuntimeException("5yrFsNrM9K");
  }

}
