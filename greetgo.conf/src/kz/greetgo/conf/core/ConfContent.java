package kz.greetgo.conf.core;

import java.util.ArrayList;
import java.util.List;

public class ConfContent {

  public List<ConfRecord> records = new ArrayList<>();

  public static ConfContent of(List<ConfRecord> records) {
    ConfContent ret = new ConfContent();
    ret.records = records;
    return ret;
  }

  public static ConfContent empty() {
    return new ConfContent();
  }

  public void appendTo(List<String> lines) {
    for (ConfRecord confRecord : records) {
      if (lines.size() > 0) {
        lines.add("");
      }
      confRecord.appendTo(lines);
    }
  }
}
