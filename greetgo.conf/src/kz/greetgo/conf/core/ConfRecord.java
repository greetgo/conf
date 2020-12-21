package kz.greetgo.conf.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Запись конфига
 */
public class ConfRecord {
  /**
   * Комментарии перед записью
   */
  public final List<String> comments = new ArrayList<>();

  /**
   * Ключ, которому присваивается значение
   * <p>
   * Если null, то данная запись содержит только комментарий
   */
  public String key;

  /**
   * Присваиваемое значение
   * <p>
   * Должен быть null, если <code>{@link #key} == null</code>
   */
  public String value;

  @Override
  public String toString() {
    List<String> ss = new ArrayList<>();

    if (key != null) {
      ss.add(value == null ? key : key + '=' + value);
    }

    String oneLineComment = oneLineComment();
    if (oneLineComment != null) {
      ss.add("// " + oneLineComment);
    }
    return getClass().getSimpleName() + '{' + String.join(" ", ss) + '}';
  }

  private String oneLineComment() {
    if (comments.isEmpty()) {
      return null;
    }
    String comment = String.join("\n", comments);
    return comment.replaceAll("\n", "\\n");
  }

  public static ConfRecord ofComment(String comment) {
    List<String> comments = comment == null ? Collections.emptyList() : Arrays.asList(comment.split("\n"));
    return ofComments(comments);
  }

  public static ConfRecord of(String key, String value, List<String> comments) {
    ConfRecord ret = new ConfRecord();
    ret.comments.addAll(comments);
    ret.key = key;
    ret.value = value;
    return ret;
  }

  public static ConfRecord of(String key, String value, String comment) {
    List<String> comments = comment == null ? Collections.emptyList() : Arrays.asList(comment.split("\n"));
    return of(key, value, comments);
  }

  public static ConfRecord ofComments(List<String> comments) {
    ConfRecord ret = new ConfRecord();
    ret.comments.addAll(comments);
    ret.key = null;
    ret.value = null;
    return ret;
  }

  public void appendTo(StringBuilder sb) {
    for (String comment : comments) {
      if (comment == null || comment.isEmpty()) {
        sb.append("#\n");
      } else {
        sb.append("# ").append(comment).append("\n");
      }
    }
    if (value != null) {
      Objects.requireNonNull(key);
      sb.append(key).append('=').append(value).append("\n");
    } else if (key != null) {
      sb.append(key).append("\n");
    }
  }
}
