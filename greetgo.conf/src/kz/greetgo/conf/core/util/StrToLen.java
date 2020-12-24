package kz.greetgo.conf.core.util;

public class StrToLen {
  public static String strToLen(String str, int length) {
    StringBuilder sb = new StringBuilder();
    if (str != null) {
      sb.append(str);
    }
    while (sb.length() < length) {
      sb.append(" ");
    }
    return sb.toString();
  }
}
