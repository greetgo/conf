package kz.greetgo.conf.core.util;

import java.util.List;

public class GetAndToLen {
  public static String getAndToLen(List<String> list, int i, int length) {
    return StrToLen.strToLen(i < list.size() ? list.get(i) : null, length);
  }
}
