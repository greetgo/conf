package kz.greetgo.conf.core;

public class ConfRecord {
  public boolean hasValue;
  public String comment;
  public String key;
  public String value;

  public static ConfRecord ofComment(String comment) {
    ConfRecord ret = new ConfRecord();
    ret.hasValue = false;
    ret.comment = comment;
    ret.key = null;
    ret.value = null;
    return ret;
  }

  public static ConfRecord of(String key, String value, String comment) {
    ConfRecord ret = new ConfRecord();
    ret.hasValue = true;
    ret.comment = comment;
    ret.key = key;
    ret.value = value;
    return ret;
  }
}
