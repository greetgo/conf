package kz.greetgo.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * <p>Perform default parameter value</p>
 * <p>You can use following variables:
 * <table>
 * <tr>
 * <td><strong>{user.name}</strong></td>
 * <td>Current system user name</td>
 * </tr>
 * <tr>
 * <td><strong>{user.home}<strong></td>
 * <td>Current system user home directory</td>
 * </tr>
 * </table>
 * <p/>
 * You can extend this list with overriding method
 * {@link AbstractConfigFactory#replaceParametersInDefaultStrValue(String)}
 * <p/>
 * </p>
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
