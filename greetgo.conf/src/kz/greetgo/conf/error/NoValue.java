package kz.greetgo.conf.error;

/**
 * Indicates that there is no value for specified element
 */
public class NoValue extends RuntimeException {
  /**
   * Path to element
   */
  public final String path;

  public NoValue(CharSequence path) {
    super("" + path);
    this.path = path == null ? null : path.toString();
  }

  public NoValue(StringBuilder prevPath, String name) {
    this(prevPath == null || prevPath.toString().trim().length() == 0 ? name : prevPath.toString() + '/' + name);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }

}
