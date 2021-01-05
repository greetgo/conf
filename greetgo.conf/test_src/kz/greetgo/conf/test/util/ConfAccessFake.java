package kz.greetgo.conf.test.util;

import kz.greetgo.conf.core.ConfAccess;
import kz.greetgo.conf.core.ConfContent;

import java.util.Date;
import java.util.function.LongSupplier;

public class ConfAccessFake implements ConfAccess {

  public ConfContent  content;
  public LongSupplier currentTimeMillis = System::currentTimeMillis;

  public int loadCallCount = 0;

  @Override
  public ConfContent load() {
    loadCallCount++;
    return content;
  }

  public int writeCallCount = 0;

  @Override
  public void write(ConfContent confContent) {
    writeCallCount++;
    content = confContent;
    if (content == null) {
      lastModifiedAt = null;
    } else {
      lastModifiedAt = new Date(currentTimeMillis.getAsLong());
    }
  }

  public Date lastModifiedAt;
  public int  lastModifiedAtCount = 0;

  @Override
  public Date lastModifiedAt() {
    lastModifiedAtCount++;
    return lastModifiedAt;
  }

  public void clearCounting() {
    lastModifiedAtCount = 0;
    writeCallCount      = 0;
    loadCallCount       = 0;
  }
}
