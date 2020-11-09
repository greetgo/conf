package kz.greetgo.conf.core;

import java.util.Date;
import java.util.List;

/**
 * Интерфейс доступа к хранилищу конфигурационного файла в структурированном виде.
 * <p>
 * Работать просто с текстом оказалось неудобно, потому что:
 * Во-первых: хранить в SQL-ой БД текст конфига неудобно, да и в MongoDB тоже.
 * Ну а во-вторых: хотелось бы уметь работать с разными форматами конфига-файла, а так привязан только к одному
 */
public interface ConfAccess {

  ConfContent load();

  void write(ConfContent confContent);

  Date lastModifiedAt();

}
