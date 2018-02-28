package kz.greetgo.conf.hot;

import java.util.List;
import java.util.stream.Collectors;

public class TooManyDefaultAnnotations extends RuntimeException {
  public final List<String> annotations;

  public TooManyDefaultAnnotations(List<String> annotations) {
    super(annotations.stream().sorted().collect(Collectors.joining(", ")));
    this.annotations = annotations;
  }
}
