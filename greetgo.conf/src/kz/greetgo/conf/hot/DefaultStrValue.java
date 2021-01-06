package kz.greetgo.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Perform default parameter value
 *
 * @author pompei
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultStrValue {

  /**
   * Perform default parameter value
   *
   * @return default parameter value
   */
  String value();

}
