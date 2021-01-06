package kz.greetgo.conf.error;

import java.lang.reflect.Method;

public class CannotConvertToType extends ConvertingError {

  public final String   convertingValue;
  public final Class<?> type;

  private static String message(String convertingValue, Class<?> type) {
    return "convertingValue = " + convertingValue + ", type = " + type;
  }

  public CannotConvertToType(String convertingValue, Class<?> type, Throwable e) {
    super(message(convertingValue, type), e);
    this.convertingValue = convertingValue;
    this.type            = type;
  }

  public CannotConvertToType(String convertingValue, Class<?> type) {
    super(message(convertingValue, type));
    this.convertingValue = convertingValue;
    this.type            = type;
  }

  public CannotConvertToType(String convertingValue, Class<?> type,
                             Class<?> configInterface, Method method,
                             CannotConvertToType cause) {

    super(message(convertingValue, type) + " ; at " + place(configInterface, method),
          cause, configInterface, method);
    this.convertingValue = convertingValue;
    this.type            = type;

  }


  @Override
  public HasConfigInterfaceAndMethod setSourcePoint(Class<?> configInterface, Method method) {
    return new CannotConvertToType(convertingValue, type, configInterface, method, this);
  }

}
