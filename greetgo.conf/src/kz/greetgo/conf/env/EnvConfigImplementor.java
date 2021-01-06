package kz.greetgo.conf.env;

import kz.greetgo.conf.ConfUtil;
import kz.greetgo.conf.error.CannotConvertToType;
import kz.greetgo.conf.hot.Description;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static kz.greetgo.conf.core.util.GetAndToLen.getAndToLen;
import static kz.greetgo.conf.core.util.SplitToList.splitToList;
import static kz.greetgo.conf.core.util.StrToLen.strToLen;

public class EnvConfigImplementor {

  private static class Param {

    final         String   methodName;
    final         String   envName;
    final         String   value;
    final         String   description;
    private final Class<?> paramType;
    public        Object   typedValue;

    public Param(String methodName, String envName, String value, String description, Class<?> paramType) {
      this.methodName  = methodName;
      this.envName     = envName;
      this.value       = value;
      this.description = description;
      this.paramType   = paramType;
    }

    final List<String> errors = new ArrayList<>();

    boolean hasErrors() {
      return errors.size() > 0;
    }

    public void init() {

      try {
        typedValue = ConfUtil.convertToType(value, paramType);
      } catch (CannotConvertToType e) {
        errors.addAll(splitToList((e.getMessage())));
        if (e.getCause() != null) {
          errors.addAll(splitToList(e.getCause().getClass().getSimpleName() + " : " + e.getCause().getMessage()));
        }
      }

      if (errors.isEmpty() && (value == null || value.trim().length() == 0)) {
        errors.add("Значение не указано либо пустое");
      }
    }

    public String methodName() {
      return methodName + "()";
    }

    public String envName() {
      return envName;
    }

    public List<String> valueList() {
      if (value == null) {
        return Collections.emptyList();
      }
      return Arrays.stream(value.split("\n")).map(String::trim).collect(toList());
    }

    public List<String> descList() {
      List<String> ret = new ArrayList<>();
      if (description != null) {
        for (String str : description.split("\n")) {
          ret.add(str.trim());
        }
      }

      if (errors.size() > 0) {

        final String ERROR = "ОШИБКА: ";
        final String SPACE = "        ";
        boolean      first = true;
        for (String error : errors) {
          ret.add((first ? ERROR : SPACE) + error);
          first = false;
        }

      }

      return ret;
    }

  }

  public static <T> T impl(Class<T> configInterface, EnvSource envSource) {

    List<Param> params = new ArrayList<>();

    for (Method method : configInterface.getMethods()) {

      EnvName envName = method.getAnnotation(EnvName.class);
      if (envName == null) {
        continue;
      }

      String value = envSource.getValue(envName.value());

      Description description = method.getAnnotation(Description.class);

      params.add(new Param(
        method.getName(),
        envName.value(),
        value,
        description == null ? "" : description.value(),
        method.getReturnType())
      );

    }

    params.forEach(Param::init);

    if (params.stream().anyMatch(Param::hasErrors)) {
      StringBuilder err = new StringBuilder();
      err.append("\n\n    Не заполнены или заполнены некорректными значениями переменные окружения\n\n");
      err.append("Конфиг: ").append(configInterface.getSimpleName()).append(" : ").append(configInterface).append("\n");
      err.append("\n");

      Description description = configInterface.getAnnotation(Description.class);
      if (description != null) {
        for (String line : description.value().split("\n")) {
          err.append("  ").append(line.trim()).append('\n');
        }
        err.append("\n");
      }

      String head1 = "Метод";
      String head2 = "Переменная окружения";
      String head3 = "Имеющееся значение";
      String head4 = "Описание и/или ошибки";

      int col1len = head1.length();
      int col2len = head2.length();
      int col3len = head3.length();
      int col4len = head4.length();

      for (Param param : params) {

        col1len = Math.max(col1len, param.methodName().length());
        col2len = Math.max(col2len, param.envName().length());

        for (String value : param.valueList()) {
          col3len = Math.max(col3len, value.length());
        }
        for (String str : param.descList()) {
          col4len = Math.max(col4len, str.length());
        }

      }

      err.append("    ").append(strToLen(head1, col1len)).append(' ');
      err.append("    ").append(strToLen(head2, col2len)).append(' ');
      err.append("    ").append(strToLen(head3, col3len)).append(' ');
      err.append("    ").append(strToLen(head4, col4len)).append('\n');

      for (Param param : params) {

        List<String> col1 = singletonList(param.methodName());
        List<String> col2 = singletonList(param.envName());
        List<String> col3 = param.valueList();
        List<String> col4 = param.descList();

        int rows = Math.max(col1.size(), Math.max(col3.size(), col4.size()));

        err.append('\n');
        for (int i = 0; i < rows; i++) {

          err.append("    ").append(getAndToLen(col1, i, col1len)).append(' ');
          err.append("    ").append(getAndToLen(col2, i, col2len)).append(' ');
          err.append("    ").append(getAndToLen(col3, i, col3len)).append(' ');
          err.append("    ").append(getAndToLen(col4, i, col4len)).append('\n');

        }

      }

      throw new IllegalEnvValues(err.toString());
    }

    Map<String, Object> methodNameValueMap = params.stream().collect(toMap(x -> x.methodName, x -> x.typedValue));

    //noinspection unchecked
    return (T) Proxy.newProxyInstance(configInterface.getClassLoader(), new Class[]{configInterface}, (proxy, method, args) -> {
      if (method.getParameterCount() != 0) {
        //noinspection SuspiciousInvocationHandlerImplementation
        return null;
      }
      String methodName = method.getName();
      if (methodName.equals("toString")) {
        return "EnvSourceConfigProxyFor-" + configInterface.getSimpleName() + "@" + System.identityHashCode(proxy);
      }

      if (methodNameValueMap.containsKey(methodName)) {
        return methodNameValueMap.get(methodName);
      }

      if (method.isDefault()) {

        // Нужно каким-то макаром вызвать default-метод интерфейса.
        // Просто вызвать этот метод нельзя, потому что вызовется опять этот handler, и далее StackOverflowException

        // Я нашёл в интернете способ как это сделать - и запихал сюда. Здесь используется
        // какая-то хитрая reflection магия бровей... я не знаю как именно, но она работает:

        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
        constructor.setAccessible(true);

        return constructor.newInstance(configInterface)
                          .in(configInterface)
                          .unreflectSpecial(method, configInterface)
                          .bindTo(proxy)
                          .invokeWithArguments();

      }

      return null;
    });
  }

}
