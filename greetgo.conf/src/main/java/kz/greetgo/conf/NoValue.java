package kz.greetgo.conf;

public class NoValue extends RuntimeException {
  public final String path;

  public NoValue(CharSequence path) {
    super("" + path);
    this.path = path == null ? null : path.toString();
  }
}
