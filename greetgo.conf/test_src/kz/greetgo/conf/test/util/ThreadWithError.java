package kz.greetgo.conf.test.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadWithError extends Thread {
  private final RunWithError runWithError;

  public ThreadWithError(RunWithError runWithError) {
    Objects.requireNonNull(runWithError);
    this.runWithError = runWithError;
  }

  private final AtomicReference<Throwable> error = new AtomicReference<>(null);

  @Override
  public void run() {
    try {
      runWithError.run();
    } catch (Throwable throwable) {
      error.set(throwable);
    }
  }

  public Throwable getError() {
    return error.get();
  }

  public void throwIfError() {
    Throwable error = getError();
    if (error == null) {
      return;
    }
    if (error instanceof RuntimeException) {
      throw (RuntimeException) error;
    }
    throw new RuntimeException(error);
  }
}
