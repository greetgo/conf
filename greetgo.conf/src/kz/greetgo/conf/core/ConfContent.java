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
}
