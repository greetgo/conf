package kz.greetgo.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Perform default list size
 *
 * @author pompei
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultListSize {

  /**
   * Perform default list size
   *
   * @return default list size
   */
  int value();

}
