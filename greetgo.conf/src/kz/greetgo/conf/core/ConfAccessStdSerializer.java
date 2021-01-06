package kz.greetgo.conf.core;

import java.util.ArrayList;
import java.util.List;

public class ConfAccessStdSerializer implements ConfContentSerializer {

  private static String uncomment(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    for (int i = 1, n = str.length(); i < n; i++) {
      if (str.charAt(i) != '#') {
        return str.substring(i);
      }
    }
    return "";
  }

  private static String killFirstSpace(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.startsWith(" ") ? str.substring(1) : str;
  }

  public ConfContent deserialize(String text) {
    if (text == null) {
      return ConfContent.empty();
    }
    List<ConfRecord> ret      = new ArrayList<>();
    List<String>     comments = new ArrayList<>();
    String[]         lines    = text.split("\n");
    for (String line : lines) {
      String trimmedLine = line.trim();
      if (trimmedLine.length() == 0) {
        if (comments.size() > 0) {
          ret.add(ConfRecord.ofComments(comments));
          comments.clear();
        }
        continue;
      }
      if (trimmedLine.startsWith("#")) {
        String comment = uncomment(trimmedLine);
        comments.add(killFirstSpace(comment));
        continue;
      }
      int i = line.indexOf('=');
      //noinspection IfStatementWithIdenticalBranches
      if (i < 0) {
        ret.add(ConfRecord.of(line, null, comments));
        comments.clear();
      } else {
        ret.add(ConfRecord.of(line.substring(0, i), line.substring(i + 1), comments));
        comments.clear();
      }
    }
    if (comments.size() > 0) {
      ret.add(ConfRecord.ofComments(comments));
    }
    return ConfContent.of(ret);
  }

  public String serialize(ConfContent confContent) {
    List<String> lines = new ArrayList<>();
    confContent.appendTo(lines);
    return String.join("\n", lines);
  }

}
