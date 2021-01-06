package kz.greetgo.conf.core;

import java.util.Map;

public class ConfContentData {
  public final Map<String, String>  params;
  public final Map<String, Integer> sizes;
  public final long                 lastModifiedAt;

  public ConfContentData(long lastModifiedAt, Map<String, String> params, Map<String, Integer> sizes) {
    this.lastModifiedAt = lastModifiedAt;
    this.params         = params;
    this.sizes          = sizes;
  }
}
