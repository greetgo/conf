package kz.greetgo.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Defined config file name.
 * <p>
 * Extension of file has been gotten from method {@link FileConfigFactory#getConfigFileExt()}
 *
 * @author pompei
 */
@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigFileName {
  String value();
}
