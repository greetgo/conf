package kz.greetgo.conf.core;

import kz.greetgo.conf.hot.CannotConvertToType;
import kz.greetgo.conf.test.util.ConfCallbackMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class ConfImplToCallbackTest_1 {

  interface Conf {

    String paramStr();

    boolean paramBool();

    Boolean paramBoolBox();

    int paramInt();

    Integer paramIntBox();

    long paramLong();

    Long paramLongBox();

    float paramFloat();

    Float paramFloatBox();

    double paramDouble();

    Double paramDoubleBox();

    BigDecimal paramBigDecimal();

    Date paramDate();

  }

  @Test
  public void paramStr() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramStr", "PVCg0jip5R");
    assertThat(impl.paramStr()).isEqualTo("PVCg0jip5R");
  }

  @Test
  public void paramBool() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramBool", "true");
    assertThat(impl.paramBool()).isTrue();

    confCallback.prm("paramBool", "false");
    assertThat(impl.paramBool()).isFalse();

    confCallback.prm("paramBool", null);
    assertThat(impl.paramBool()).isFalse();
  }

  @Test
  public void paramBoolBox() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramBoolBox", "true");
    assertThat(impl.paramBoolBox()).isTrue();

    confCallback.prm("paramBoolBox", "false");
    assertThat(impl.paramBoolBox()).isFalse();

    confCallback.prm("paramBoolBox", null);
    assertThat(impl.paramBoolBox()).isNull();
  }

  @Test
  public void paramInt() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramInt", "432154");
    assertThat(impl.paramInt()).isEqualTo(432154);

    confCallback.prm("paramInt", "-432617527");
    assertThat(impl.paramInt()).isEqualTo(-432617527);

    confCallback.prm("paramInt", null);
    assertThat(impl.paramInt()).isEqualTo(0);

    confCallback.prm("paramInt", "true");
    assertThat(impl.paramInt()).isEqualTo(1);

    confCallback.prm("paramInt", "false");
    assertThat(impl.paramInt()).isEqualTo(0);

  }

  @Test
  public void paramIntBox() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramIntBox", "432154");
    assertThat(impl.paramIntBox()).isEqualTo(432154);

    confCallback.prm("paramIntBox", "-432617527");
    assertThat(impl.paramIntBox()).isEqualTo(-432617527);

    confCallback.prm("paramIntBox", null);
    assertThat(impl.paramIntBox()).isNull();

  }

  @Test
  public void call__equals() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    impl.equals(null);

  }

  @Test
  public void call__hashCode() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    impl.hashCode();

  }

  @Test
  public void paramFloat() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramFloat", "4325.456");
    assertThat(impl.paramFloat()).isEqualTo(4325.456f);

    confCallback.prm("paramFloat", "-6543.12");
    assertThat(impl.paramFloat()).isEqualTo(-6543.12f);

    confCallback.prm("paramFloat", null);
    assertThat(impl.paramFloat()).isEqualTo(0f);

    confCallback.prm("paramFloat", "true");
    assertThat(impl.paramFloat()).isEqualTo(1f);

    confCallback.prm("paramFloat", "false");
    assertThat(impl.paramFloat()).isEqualTo(0f);

  }

  @Test
  public void paramFloatBox() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramFloatBox", "4325.456");
    assertThat(impl.paramFloatBox()).isEqualTo(4325.456f);

    confCallback.prm("paramFloatBox", "-6543.12");
    assertThat(impl.paramFloatBox()).isEqualTo(-6543.12f);

    confCallback.prm("paramFloatBox", null);
    assertThat(impl.paramFloatBox()).isNull();

  }

  @Test(expectedExceptions = CannotConvertToType.class)
  public void paramFloat__CannotConvertToType() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramFloat", "4h3b24b");
    impl.paramFloat();

  }

  @Test(expectedExceptions = CannotConvertToType.class)
  public void paramDouble__CannotConvertToType() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramDouble", "4h3b24b");
    impl.paramDouble();

  }

  @Test
  public void paramDouble() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramDouble", "4325.456");
    assertThat(impl.paramDouble()).isEqualTo(4325.456d);

    confCallback.prm("paramDouble", "-6543.12");
    assertThat(impl.paramDouble()).isEqualTo(-6543.12d);

    confCallback.prm("paramDouble", null);
    assertThat(impl.paramDouble()).isEqualTo(0d);

    confCallback.prm("paramDouble", "true");
    assertThat(impl.paramDouble()).isEqualTo(1d);

    confCallback.prm("paramDouble", "false");
    assertThat(impl.paramDouble()).isEqualTo(0d);

  }

  @Test
  public void paramDoubleBox() {

    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("paramDoubleBox", "4325.456");
    assertThat(impl.paramDoubleBox()).isEqualTo(4325.456d);

    confCallback.prm("paramDoubleBox", "-6543.12");
    assertThat(impl.paramDoubleBox()).isEqualTo(-6543.12d);

    confCallback.prm("paramDoubleBox", null);
    assertThat(impl.paramDoubleBox()).isNull();

  }

}
