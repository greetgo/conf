package kz.greetgo.conf.core;

public class ConfCallbackPrefix implements ConfCallback {

  private final String       prefix;
  private final ConfCallback source;

  public ConfCallbackPrefix(String prefix, ConfCallback source) {
    this.prefix = prefix;
    this.source = source;
  }

  @Override
  public String readParam(String paramPath) {
    return source.readParam(prefix + paramPath);
  }

  @Override
  public int readParamSize(String paramPath) {
    return source.readParamSize(prefix + paramPath);
  }

  @Override
  public String readEnv(String envName) {
    throw new RuntimeException("KF1dPxOt1k :: "
                                 + "Access to environment variables cannot be used inside hierarchy. "
                                 + "envName = " + envName + ", source = " + source);
  }
}
