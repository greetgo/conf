package kz.greetgo.conf.core;

import kz.greetgo.conf.ConfUtil;
import kz.greetgo.conf.core.fields.ConfIgnore;
import kz.greetgo.conf.core.fields.FieldAccess;
import kz.greetgo.conf.core.fields.FieldDef;
import kz.greetgo.conf.core.fields.FieldDefParserDefault;
import kz.greetgo.conf.core.fields.FieldParserDefault;
import kz.greetgo.conf.hot.DefaultListSize;
import kz.greetgo.conf.hot.Description;
import kz.greetgo.conf.hot.FirstReadEnv;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static kz.greetgo.conf.ConfUtil.convertToType;
import static kz.greetgo.conf.ConfUtil.convertibleTypeList;
import static kz.greetgo.conf.ConfUtil.extractStrDefaultValue;
import static kz.greetgo.conf.ConfUtil.findAnnotation;
import static kz.greetgo.conf.ConfUtil.isConvertingType;
import static kz.greetgo.conf.core.util.UnionDescriptions.unionDescriptions;

public class ConfImplToCallback<T> {
  private final Class<T>     interfaceClass;
  private final ConfCallback confCallback;
  private final T            impl;

  public ConfImplToCallback(Class<T> interfaceClass, ConfCallback confCallback) {
    this.interfaceClass = interfaceClass;
    this.confCallback   = confCallback;
    //noinspection unchecked
    impl = (T) Proxy.newProxyInstance(
      getClass().getClassLoader(),
      new Class[]{interfaceClass},
      this::invokeInterfaceMethod);
  }

  public T impl() {
    return impl;
  }

  private final ConcurrentHashMap<String, List<ConfImplToCallback<?>>> subCallbackLists  = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, ConfImplToCallback<?>>       subCallbacks      = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Supplier<Object>>            byMethodSuppliers = new ConcurrentHashMap<>();

  public Object invokeInterfaceMethod(Object proxy, Method method, Object[] args) throws Throwable {
    String   methodName = method.getName();
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

    if ("toString".equals(methodName)) {
      return "HotConfigProxy(" + interfaceClass.getName() + "->" + this + ")@" + System.identityHashCode(proxy);
    }

    {
      Supplier<Object> supplier = byMethodSuppliers.get(methodName);
      if (supplier != null) {
        return supplier.get();
      }
    }

    if (isConvertingType(returnType)) {
      FirstReadEnv firstReadEnv = method.getAnnotation(FirstReadEnv.class);
      if (firstReadEnv != null) {
        String envValue = confCallback.readEnv(firstReadEnv.value());
        if (envValue != null && envValue.trim().length() > 0) {
          Object returnValue = convertToType(envValue, returnType);
          byMethodSuppliers.put(methodName, () -> returnValue);
          return returnValue;
        }
      }
      {
        Supplier<Object> supplier = () -> {
          String strValue = confCallback.readParam(methodName);
          return convertToType(strValue, returnType);
        };

        byMethodSuppliers.put(methodName, supplier);
        return supplier.get();
      }
    }

    if (returnType.isEnum()) {
      FirstReadEnv firstReadEnv = method.getAnnotation(FirstReadEnv.class);
      if (firstReadEnv != null) {
        String envValue = confCallback.readEnv(firstReadEnv.value());
        if (envValue != null && envValue.trim().length() > 0) {
          T returnValue = ConfUtil.valueOfEnum(envValue, returnType);
          byMethodSuppliers.put(methodName, () -> returnValue);
          return returnValue;
        }
      }
      {
        Supplier<Object> supplier = () -> {
          String strValue = confCallback.readParam(methodName);
          return ConfUtil.valueOfEnum(strValue, returnType);
        };

        byMethodSuppliers.put(methodName, supplier);
        return supplier.get();
      }
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
        ConfCallbackPrefix    callbackPrefix = new ConfCallbackPrefix(method.getName() + '.', confCallback);
        ConfImplToCallback<?> ret            = new ConfImplToCallback<>(method.getReturnType(), callbackPrefix);
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

    throw new RuntimeException("spE6Q3TS9n :: Cannot extractFirstArgumentClass from " + type);
  }

  public ConfContent defaultContent() {
    // этот метод кэшировать НЕ нужно
    ConfContent confContent = new ConfContent();
    appendContent(confContent, null, null);
    return confContent;
  }

  private void appendContent(ConfContent confContent, String prefix, Description superDescription) {
    {
      Description  description      = findAnnotation(interfaceClass, Description.class);
      List<String> descriptionLines = unionDescriptions(description, superDescription);

      if (description != null) {
        if (prefix == null) {
          confContent.records.add(ConfRecord.ofComments(descriptionLines));
        } else {
          confContent.records.add(ConfRecord.of(prefix, null, descriptionLines));
        }
      }
    }

    for (Method method : interfaceClass.getMethods()) {

      Class<?>    returnType   = method.getReturnType();
      String      name         = (prefix == null ? "" : (prefix + '.')) + method.getName();
      Description description  = method.getAnnotation(Description.class);
      String      defaultValue = extractStrDefaultValue(method.getAnnotations(), returnType);

      if (isConvertingType(returnType)) {
        confContent.records.add(ConfRecord.ofDescription(name, defaultValue, description));
        continue;
      }

      if (List.class.isAssignableFrom(returnType)) {
        Class<?>        argClass        = extractFirstArgumentClass(method.getAnnotatedReturnType().getType());
        DefaultListSize defaultListSize = method.getAnnotation(DefaultListSize.class);
        int             listSize        = defaultListSize == null ? 1 : defaultListSize.value();

        if (isConvertingType(argClass)) {

          confContent.records.add(ConfRecord.ofDescription(name + ".0", defaultValue, description));
          for (int i = 1; i < listSize; i++) {
            confContent.records.add(ConfRecord.of(name + '.' + i, defaultValue));
          }

          continue;
        }

        if (argClass.isInterface()) {

          for (int i = 0; i < listSize; i++) {

            ConfCallbackPrefix    callbackPrefix = new ConfCallbackPrefix(name + '.' + i + '.', confCallback);
            ConfImplToCallback<?> callback       = new ConfImplToCallback<>(argClass, callbackPrefix);

            callback.appendContent(confContent, name + '.' + i, description);

          }

          continue;
        }

        throw new RuntimeException("szOckL3MyQ :: " + method);
      }

      if (returnType.isEnum()) {

        List<String> descriptionList = new ArrayList<>();
        descriptionList.add("Enum " + returnType.getSimpleName() + " with values:");
        if (description != null) {
          descriptionList.addAll(Arrays.asList(description.value().split("\n")));
        }

        try {
          Object values = returnType.getMethod("values").invoke(null);

          //noinspection rawtypes
          Arrays.stream(((Enum[]) values))
                .map(Enum::name)
                .map(x -> "  = " + x)
                .forEach(descriptionList::add);

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          throw new RuntimeException(e);
        }

        confContent.records.add(ConfRecord.of(name, defaultValue, descriptionList));

        continue;
      }


      if (returnType.isInterface()) {

        ConfCallbackPrefix    callbackPrefix = new ConfCallbackPrefix(method.getName() + '.', confCallback);
        ConfImplToCallback<?> callback       = new ConfImplToCallback<>(method.getReturnType(), callbackPrefix);

        callback.appendContent(confContent, name, description);

        continue;
      }

      {
        List<String> lines = unionDescriptions(findAnnotation(returnType, Description.class), description);
        confContent.records.add(ConfRecord.of(name, null, lines));

        for (FieldDef fd : FieldDefParserDefault.instance.parse(returnType)) {
          confContent.records.add(ConfRecord.of(name + '.' + fd.name(), fd.defaultValue(), fd.descriptionLines()));
        }

        continue;
      }

    }
  }

}
