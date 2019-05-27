package kz.greetgo.conf.hot;

import java.lang.reflect.Method;

public class CannotWorkWithType extends HasConfigInterfaceAndMethod {
  public final Class<?> workingType;

  public CannotWorkWithType(Class<?> workingType, Class<?> configInterface, Method method, Throwable cause) {
    super("workingType = " + workingType + "; at " + configInterface.getSimpleName() + "." + method.getName(),
      cause, configInterface, method);
    this.workingType = workingType;
  }

  public CannotWorkWithType(Class<?> workingType) {
    super("workingType = " + workingType);
    this.workingType = workingType;
  }

  @Override
  public HasConfigInterfaceAndMethod setSourcePoint(Class<?> configInterface, Method method) {
    return new CannotWorkWithType(workingType, configInterface, method, this);
  }

}
