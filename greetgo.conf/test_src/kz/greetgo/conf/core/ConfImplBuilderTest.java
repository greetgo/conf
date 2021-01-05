package kz.greetgo.conf.core;

import kz.greetgo.conf.test.util.ConfAccessFake;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfImplBuilderTest {

  @SuppressWarnings("UnusedReturnValue")
  interface TestConfig {
    String param1();

    int param2();
  }

  @Test
  public void build__simpleTiming() {

    AtomicLong time = new AtomicLong(1000);

    ConfAccessFake confAccess = new ConfAccessFake();
    confAccess.content = new ConfContent();
    confAccess.content.records.add(ConfRecord.of("param1", "test value"));
    confAccess.content.records.add(ConfRecord.of("param2", "543156"));
    confAccess.clearCounting();

    ConfImplBuilder<TestConfig> builder = ConfImplBuilder.confImplBuilder(TestConfig.class, confAccess)
                                                         .currentTimeMillis(time::get)
                                                         .changeCheckTimeoutMs(3000);

    //
    //
    TestConfig testConfig = builder.build();
    //
    //

    assertThat(confAccess.loadCallCount).isZero();

    String toString = testConfig.toString();

    assertThat(confAccess.loadCallCount).isZero();

    assertThat(toString).endsWith("@" + System.identityHashCode(testConfig));
    assertThat(toString).contains(TestConfig.class.getName());

    testConfig.param1();

    assertThat(confAccess.lastModifiedAtCount).isEqualTo(2);
    assertThat(confAccess.loadCallCount).isEqualTo(1);

    time.addAndGet(1000);

    testConfig.param1();

    assertThat(confAccess.lastModifiedAtCount).isEqualTo(2);
    assertThat(confAccess.loadCallCount).isEqualTo(1);

    testConfig.param2();

    assertThat(confAccess.lastModifiedAtCount).isEqualTo(2);
    assertThat(confAccess.loadCallCount).isEqualTo(1);

    time.addAndGet(3001);

    testConfig.param1();
    testConfig.param2();

    assertThat(confAccess.lastModifiedAtCount).isEqualTo(3);
    assertThat(confAccess.loadCallCount).isEqualTo(1);
  }

  @Test
  public void build__equableTiming() {

    AtomicLong time = new AtomicLong(1000);

    ConfAccessFake confAccess = new ConfAccessFake();
    confAccess.currentTimeMillis = time::get;

    ConfImplBuilder<TestConfig> builder = ConfImplBuilder.confImplBuilder(TestConfig.class, confAccess)
                                                         .currentTimeMillis(time::get)
                                                         .changeCheckTimeoutMs(3000);

    //
    //
    TestConfig testConfig = builder.build();
    //
    //

    assertThat(confAccess.loadCallCount).isZero();

    String toString = testConfig.toString();

    assertThat(confAccess.loadCallCount).isZero();

    assertThat(toString).endsWith("@" + System.identityHashCode(testConfig));
    assertThat(toString).contains(TestConfig.class.getName());

    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(2);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(2);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(2);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(3);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(3);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(3);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(4);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(4);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(4);

    time.addAndGet(1001);
    testConfig.param1();
    assertThat(confAccess.lastModifiedAtCount).isEqualTo(5);
  }

  @Test
  public void build__timingWithModifications() {

    AtomicLong time = new AtomicLong(1000);

    ConfAccessFake confAccess = new ConfAccessFake();
    confAccess.currentTimeMillis = time::get;

    confAccess.content = new ConfContent();
    confAccess.content.records.add(ConfRecord.of("param1", "test value"));
    confAccess.content.records.add(ConfRecord.of("param2", "543156"));

    confAccess.clearCounting();

    ConfImplBuilder<TestConfig> builder = ConfImplBuilder.confImplBuilder(TestConfig.class, confAccess)
                                                         .currentTimeMillis(time::get)
                                                         .changeCheckTimeoutMs(3000);

    //
    //
    TestConfig testConfig = builder.build();
    //
    //

    assertThat(confAccess.loadCallCount).isZero();

    String toString = testConfig.toString();

    assertThat(confAccess.loadCallCount).isZero();

    assertThat(toString).endsWith("@" + System.identityHashCode(testConfig));
    assertThat(toString).contains(TestConfig.class.getName());

    testConfig.param1();

    assertThat(confAccess.lastModifiedAtCount).isEqualTo(2);
    assertThat(confAccess.loadCallCount).isEqualTo(1);

    time.addAndGet(4000);

    testConfig.param1();

    assertThat(confAccess.lastModifiedAtCount).isEqualTo(3);
    assertThat(confAccess.loadCallCount).isEqualTo(1);

    time.addAndGet(1000);
    confAccess.lastModifiedAt = new Date(time.get());

    testConfig.param1();

    assertThat(confAccess.lastModifiedAtCount).isEqualTo(3);
    assertThat(confAccess.loadCallCount).isEqualTo(1);

    time.addAndGet(4000);

    testConfig.param1();

    assertThat(confAccess.lastModifiedAtCount).isEqualTo(4);
    assertThat(confAccess.loadCallCount).isEqualTo(2);
  }

}
