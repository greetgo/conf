package kz.greetgo.conf.core.util;

import java.util.ArrayList;
import java.util.List;

public class SplitToList {
  public static List<String> splitToList(String str) {
    List<String> ret = new ArrayList<>();
    if (str != null) {
      for (String s : str.split("\n")) {
        ret.add(s.trim());
      }
    }
    return ret;
  }
}
