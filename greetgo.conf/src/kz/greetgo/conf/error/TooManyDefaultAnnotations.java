package kz.greetgo.conf.error;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class TooManyDefaultAnnotations extends HasConfigInterfaceAndMethod {

  public final List<String> annotations;

  private static String message(List<String> annotations) {
    return annotations.stream().sorted().collect(Collectors.joining(", "));
  }

  public TooManyDefaultAnnotations(List<String> annotations) {
    super(message(annotations));
    this.annotations = annotations;
  }

  private TooManyDefaultAnnotations(TooManyDefaultAnnotations cause,
                                    Class<?> configInterface,
                                    Method method) {
    super(message(cause.annotations) + " ; at " + place(configInterface, method), cause, configInterface, method);
    this.annotations = cause.annotations;
  }

  @Override
  public HasConfigInterfaceAndMethod setSourcePoint(Class<?> configInterface, Method method) {
    return new TooManyDefaultAnnotations(this, configInterface, method);
  }

}
