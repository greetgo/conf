package kz.greetgo.conf.hot;

import java.lang.reflect.Method;

public abstract class ConvertingError extends HasConfigInterfaceAndMethod {
  protected ConvertingError(String message, Throwable cause, Class<?> configInterface, Method method) {
    super(message, cause, configInterface, method);
  }

  protected ConvertingError(String message) {
    super(message);
  }

  public ConvertingError(String message, Throwable cause) {
    super(message, cause);
  }

}
