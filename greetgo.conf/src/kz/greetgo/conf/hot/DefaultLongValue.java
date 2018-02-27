package kz.greetgo.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Perform default parameter value
 *
 * @author pompei
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultLongValue {
  /**
   * Perform default parameter value
   *
   * @return default parameter value
   */
  long value();
}
