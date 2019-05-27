package kz.greetgo.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * <p>
 * This annotation describes from java about config parameter, if this annotation located above method.
 * </p>
 * <p>
 * This annotation describes from java about config interface, if this annotation located above interface.
 * </p>
 *
 * @author pompei
 */
@Documented
@Target({METHOD, TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

  /**
   * Description contents
   *
   * @return description contents
   */
  String value();

}
