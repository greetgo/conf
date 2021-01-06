package kz.greetgo.conf.hot;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * <p>
 * If config file is absent, then it must be created with default values. This file creates when
 * the first value is reading. But some times you can need to create config file when application is starting.
 * If you want to do this, mark such config interface with this annotation.
 * </p>
 *
 * @author pompei
 */
@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForcibleInit {}
