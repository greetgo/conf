package kz.greetgo.conf.hot;

import java.util.List;

public interface ReadElement {
  String fieldName();

  Object fieldValue();

  List<ConfigLine> createListConfigLines(int index);
}
