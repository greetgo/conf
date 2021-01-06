package kz.greetgo.conf.hot;

import java.util.List;

public interface HotConfigWithDefaultListSize {
  @DefaultListSize(9)
  @DefaultLongValue(70078)
  List<Long> longList();

  @DefaultListSize(7)
  List<ConfigElement> classList();
}
