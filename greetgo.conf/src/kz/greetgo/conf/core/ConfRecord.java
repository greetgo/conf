package kz.greetgo.conf.core;

import kz.greetgo.conf.hot.Description;

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
  private String key;

  /**
   * Присваиваемое значение
   * <p>
   * Должен быть null, если <code>{@link #key} == null</code>
   */
  private String value;

  public String trimmedKey() {
    String x = key;
    if (x == null) return null;
    String xx = x.trim();
    return xx.isEmpty() ? null : xx;
  }

  public String trimmedValue() {
    String x = value;
    return x == null ? null : x.trim();
  }

  public String key() {
    return key;
  }

  public String value() {
    return value;
  }

  @Override
  public String toString() {
    List<String> ss = new ArrayList<>();

    if (key != null) {
      ss.add(value == null ? '`' + key + '`' : '`' + key + '`' + '=' + '`' + value + '`');
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
    ret.key   = key;
    ret.value = value;
    return ret;
  }

  public static ConfRecord ofDescription(String key, String value, Description description) {
    return of(key, value, description == null ? null : description.value());
  }

  public static ConfRecord of(String key, String value, String comment) {
    List<String> comments = comment == null ? Collections.emptyList() : Arrays.asList(comment.split("\n"));
    return of(key, value, comments);
  }

  public static ConfRecord of(String key, String value) {
    return of(key, value, Collections.emptyList());
  }

  public static ConfRecord ofComments(List<String> comments) {
    ConfRecord ret = new ConfRecord();
    ret.comments.addAll(comments);
    ret.key   = null;
    ret.value = null;
    return ret;
  }

  public void appendTo(List<String> lines) {
    for (String comment : comments) {
      if (comment == null || comment.isEmpty()) {
        lines.add("#");
      } else {
        lines.add("# " + comment);
      }
    }
    if (value != null) {
      Objects.requireNonNull(key);
      lines.add(key + '=' + value);
    } else if (key != null) {
      lines.add(key);
    }
  }

  public void insertTopComment(String comment) {
    List<String> addingComments      = ConfRecord.ofComment(comment).comments;
    int          addingCommentsCount = addingComments.size();
    for (int i = 0; i < addingCommentsCount; i++) {
      comments.add(0, null);
    }
    for (int i = 0; i < addingCommentsCount; i++) {
      comments.set(i, addingComments.get(i));
    }
  }

  public String commentValue() {
    if (comments.isEmpty()) return null;
    return String.join("\n", comments);
  }
}
