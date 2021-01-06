package kz.greetgo.conf.core;

import kz.greetgo.conf.core.util.CalculateSizes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

  public void insertTopComment(String comment) {
    if (records.isEmpty()) {
      records.add(ConfRecord.ofComment(comment));
      return;
    }
    if (records.get(0).trimmedKey() != null) {
      records.add(0, ConfRecord.ofComment(comment));
      return;
    }
    records.get(0).insertTopComment(comment);
  }

  public void addComment(String comment) {
    records.add(ConfRecord.ofComment(comment));
  }

  public ConfContentData toData(long lastModifiedAt) {
    Map<String, String> params = new HashMap<>();

    for (ConfRecord record : records) {
      if (record.trimmedKey() != null) {
        params.put(record.trimmedKey(), record.trimmedValue());
      }
    }

    Map<String, Integer> sizes = CalculateSizes.of(params.keySet());

    return new ConfContentData(lastModifiedAt, Collections.unmodifiableMap(params), Collections.unmodifiableMap(sizes));
  }

  public ConfContent minus(ConfContent mini) {

    Set<String> miniKeys = mini.records.stream()
                                       .map(ConfRecord::trimmedKey)
                                       .filter(Objects::nonNull)
                                       .collect(Collectors.toSet());

    return of(records.stream()
                     .filter(x -> x.trimmedKey() != null)
                     .filter(x -> !miniKeys.contains(x.trimmedKey()))
                     .collect(Collectors.toList()));
  }

  public boolean isEmpty() {
    return records.isEmpty();
  }

  public void add(ConfContent a) {
    records.addAll(a.records);
  }
}
