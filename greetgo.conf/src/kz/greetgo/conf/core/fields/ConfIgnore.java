package kz.greetgo.conf.core.fields;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Игнорировать это поле при заполнении данных конфига
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfIgnore {}
