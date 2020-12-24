package kz.greetgo.conf.core.util;

public class MethodName {

  public static String extractGet(String getMethodName) {

    if (getMethodName.startsWith("is")) {
      return StrLow.first(getMethodName.substring(2));
    }

    if (getMethodName.startsWith("get")) {
      return StrLow.first(getMethodName.substring(3));
    }

    return null;
  }

  public static String extractSet(String getMethodName) {

    if (getMethodName.startsWith("set")) {
      return StrLow.first(getMethodName.substring(3));
    }

    return null;
  }

}
