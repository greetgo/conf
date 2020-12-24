package kz.greetgo.conf.env;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Прикрепляет метод конфига к переменной окружения
 */
@Documented
@Target({METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvName {

  /**
   * @return имя переменной окружения
   */
  String value();

}
