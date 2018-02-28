package kz.greetgo.conf.hot;

import java.util.Date;
import java.util.Map;

class LoadingLines {
  private final Date now;

  LoadingLines(Date now) {
    this.now = now;
  }

  interface Line {

  }

  void putDefinition(ElementDefinition ed) {

  }

  void readLine(String key, String value, boolean hasComment) {}

  void putData(Map<String, Object> target) {}

  boolean didContentChange() {
    return false;
  }

  String content() {
    return null;
  }
}
