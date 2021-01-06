package kz.greetgo.conf.error;

import java.lang.reflect.Method;
import java.util.List;

public class CannotDetectDateFormat extends ConvertingError {

  public final String       paringStr;
  public final List<String> patternFormatList;

  public CannotDetectDateFormat(String paringStr, List<String> patternFormatList) {
    super(message(paringStr, patternFormatList));
    this.paringStr         = paringStr;
    this.patternFormatList = patternFormatList;
  }

  private CannotDetectDateFormat(String paringStr, List<String> patternFormatList,
                                 Class<?> configInterface, Method method,
                                 CannotDetectDateFormat cause) {
    super(
      message(paringStr, patternFormatList) + " ; at " + place(configInterface, method),
      cause,
      configInterface,
      method
    );

    this.paringStr         = paringStr;
    this.patternFormatList = patternFormatList;
  }

  private static String message(String paringStr, List<String> patternFormatList) {
    return "paringStr = " + paringStr + ", must be one from " + String.join(", ", patternFormatList);
  }

  @Override
  public HasConfigInterfaceAndMethod setSourcePoint(Class<?> configInterface, Method method) {
    return new CannotDetectDateFormat(paringStr, patternFormatList, configInterface, method, this);
  }

}
