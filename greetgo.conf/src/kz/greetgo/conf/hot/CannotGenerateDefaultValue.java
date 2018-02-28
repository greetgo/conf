package kz.greetgo.conf.hot;

import java.lang.reflect.Method;

public class CannotGenerateDefaultValue extends RuntimeException {
  public final Class<?> returnType;
  public final String strValue;
  public final Class<?> configInterface;
  public final Method method;

  public CannotGenerateDefaultValue(Class<?> returnType, String strValue, Class<?> configInterface, Method method) {
    super("workingType = " + returnType + "; at " + configInterface.getSimpleName() + "." + method.getName());
    this.returnType = returnType;
    this.strValue = strValue;
    this.configInterface = configInterface;
    this.method = method;
  }

  public CannotGenerateDefaultValue(Class<?> returnType, String strValue) {
    //noinspection ConstantConditions
    this(returnType, strValue, null, null);
  }
}
