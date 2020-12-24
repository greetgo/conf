package kz.greetgo.conf.core.util;

public class StrLow {
  public static String first(String str) {
    if (str == null) return null;
    if (str.length() <= 1) return str.toLowerCase();
    return str.substring(0, 1).toLowerCase() + str.substring(1);
  }
}
