package kz.greetgo.conf.error;

import java.lang.reflect.Method;

public abstract class HasConfigInterfaceAndMethod extends RuntimeException {

  public final Class<?> configInterface;
  public final Method   method;

  protected HasConfigInterfaceAndMethod(String message, Throwable cause,
                                        Class<?> configInterface, Method method) {
    super(message, cause);
    this.configInterface = configInterface;
    this.method          = method;
  }

  protected HasConfigInterfaceAndMethod(String message) {
    super(message);
    this.configInterface = null;
    this.method          = null;
  }

  public HasConfigInterfaceAndMethod(String message, Throwable cause) {
    super(message, cause);
    this.configInterface = null;
    this.method          = null;
  }

  public abstract HasConfigInterfaceAndMethod setSourcePoint(Class<?> configInterface, Method method);

  protected static String place(Class<?> configInterface, Method method) {
    return configInterface.getName() + "." + method.getName() + "()";
  }

}
