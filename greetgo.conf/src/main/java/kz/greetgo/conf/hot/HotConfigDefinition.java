package kz.greetgo.conf.hot;

import java.util.Iterator;

public interface HotConfigDefinition extends Iterable<HotElementDefinition> {
  String location();

  int elementCount();

  HotElementDefinition element(int index);
}
