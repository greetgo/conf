package kz.greetgo.conf.env;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Прикрепляет метод конфига к переменной окружения
 */
@Documented
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvName {

  /**
   * @return имя переменной окружения
   */
  String value();

}
