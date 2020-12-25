package kz.greetgo.conf.hot;

import java.util.List;

@SuppressWarnings("unused")
public interface HostConfigWithLists {

  ConfigElement elementA();

  List<ConfigElement> elementB();

  int status();

}
