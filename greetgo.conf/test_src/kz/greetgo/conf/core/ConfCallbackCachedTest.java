package kz.greetgo.conf.core;

import kz.greetgo.conf.RND;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfCallbackCachedTest {

  public static class ConfCallbackFake implements ConfCallback {

    public String readParam;
    public String readParam_paramPath;
    public int    readParamCount = 0;

    @Override
    public String readParam(String paramPath) {
      this.readParam_paramPath = paramPath;
      readParamCount++;
      return readParam;
    }

    public int    readParamSize;
    public int    readParamSizeCount = 0;
    public String readParamSize_paramPath;

    @Override
    public int readParamSize(String paramPath) {
      readParamSize_paramPath = paramPath;
      readParamSizeCount++;
      return readParamSize;
    }

    @Override
    public String readEnv(String envName) {
      throw new RuntimeException("Kk32tiy2Xc :: Not impl yet: ConfCallbackFake.readEnv");
    }
  }

  @Test
  public void readParam() {

    ConfCallbackFake fake = new ConfCallbackFake();

    AtomicLong currentTime = new AtomicLong(1000);

    //
    //
    ConfCallbackCached cached = new ConfCallbackCached(fake, 3000, currentTime::get);
    //
    //

    fake.readParam     = RND.str(10);
    fake.readParamSize = RND.intOf(0, 100_000_000);
    String path1 = RND.str(10);
    String path2 = RND.str(10);

    assertThat(cached.readParam(path1)).isEqualTo(fake.readParam);
    assertThat(cached.readParamSize(path2)).isEqualTo(fake.readParamSize);

    assertThat(fake.readParam_paramPath).isEqualTo(path1);
    assertThat(fake.readParamSize_paramPath).isEqualTo(path2);
    assertThat(fake.readParamCount).isEqualTo(1);
    assertThat(fake.readParamSizeCount).isEqualTo(1);

    cached.readParam(path1);
    cached.readParamSize(path2);

    assertThat(fake.readParamCount).isEqualTo(1);
    assertThat(fake.readParamSizeCount).isEqualTo(1);

    currentTime.addAndGet(1000); //ещё 3000 не прошло

    cached.readParam(path1);
    cached.readParamSize(path2);

    assertThat(fake.readParamCount).describedAs("ещё 3000 не прошло и поэтому чтения не должно быть").isEqualTo(1);
    assertThat(fake.readParamSizeCount).describedAs("ещё 3000 не прошло и поэтому чтения не должно быть").isEqualTo(1);

    currentTime.addAndGet(3001); //3000 прошло

    cached.readParam(path1);
    cached.readParamSize(path2);

    assertThat(fake.readParamCount).describedAs("3000 прошло и поэтому должно прочитаться ещё раз").isEqualTo(2);
    assertThat(fake.readParamSizeCount).describedAs("3000 прошло и поэтому должно прочитаться ещё раз").isEqualTo(2);

    currentTime.addAndGet(1000); //ещё 3000 не прошло с последнего чтения

    cached.readParam(path1);
    cached.readParamSize(path2);

    assertThat(fake.readParamCount).describedAs("ещё 3000 не прошло и поэтому третьего чтения не должно быть").isEqualTo(2);
    assertThat(fake.readParamSizeCount).describedAs("ещё 3000 не прошло и поэтому третьего чтения не должно быть").isEqualTo(2);

    currentTime.addAndGet(3001); //3000 прошло

    cached.readParam(path1);
    cached.readParamSize(path2);

    assertThat(fake.readParamCount).isEqualTo(3);
    assertThat(fake.readParamSizeCount).isEqualTo(3);
  }

  @Test
  public void readParam__readParam__lowRead() {

    ConfCallbackFake fake = new ConfCallbackFake();

    AtomicLong currentTime = new AtomicLong(1000);

    //
    //
    ConfCallbackCached cached = new ConfCallbackCached(fake, 3000, currentTime::get);
    //
    //

    fake.readParam = RND.str(10);
    String path1 = RND.str(10);

    cached.readParam(path1);
    assertThat(fake.readParamCount).isEqualTo(1);

    currentTime.addAndGet(1001);
    cached.readParam(path1);
    assertThat(fake.readParamCount).isEqualTo(1);

    currentTime.addAndGet(1001);
    cached.readParam(path1);
    assertThat(fake.readParamCount).isEqualTo(1);

    currentTime.addAndGet(1001);
    cached.readParam(path1);
    assertThat(fake.readParamCount).isEqualTo(2);

    currentTime.addAndGet(1001);
    cached.readParam(path1);
    assertThat(fake.readParamCount).isEqualTo(2);

    currentTime.addAndGet(1001);
    cached.readParam(path1);
    assertThat(fake.readParamCount).isEqualTo(2);

    currentTime.addAndGet(1001);
    cached.readParam(path1);
    assertThat(fake.readParamCount).isEqualTo(3);

    currentTime.addAndGet(1001);
    cached.readParam(path1);
    assertThat(fake.readParamCount).isEqualTo(3);

  }


}
