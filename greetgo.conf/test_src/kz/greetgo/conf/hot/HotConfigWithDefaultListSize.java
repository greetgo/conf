package kz.greetgo.conf.hot;

import java.util.List;

public interface HotConfigWithDefaultListSize {
  @DefaultListSize(9)
  @DefaultIntValue(70078)
  List<Long> longList();

  @DefaultListSize(7)
  List<ConfigElement> classList();
}
