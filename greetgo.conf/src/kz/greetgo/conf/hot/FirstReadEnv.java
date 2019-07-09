package kz.greetgo.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Reads this parameter from environment variable firstly.
 *
 * @author pompei
 */
@Documented
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstReadEnv {

  /**
   * Environment variable name
   *
   * @return environment variable name
   */
  String value();

}
