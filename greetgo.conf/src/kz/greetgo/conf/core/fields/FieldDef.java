package kz.greetgo.conf.core.fields;

import java.util.List;

public interface FieldDef {

  String name();

  List<String> descriptionLines();

  String defaultValue();

}
