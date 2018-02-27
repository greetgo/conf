package kz.greetgo.conf.hot;

import java.lang.reflect.Method;

public class CannotGenerateDefaultValue extends RuntimeException {
  public final Class<?> returnType;
  public final Class<?> configInterface;
  public final Method method;

  public CannotGenerateDefaultValue(Class<?> returnType, Class<?> configInterface, Method method) {
    super("returnType = " + returnType + "; at " + configInterface.getSimpleName() + "." + method.getName());
    this.returnType = returnType;
    this.configInterface = configInterface;
    this.method = method;
  }
}
