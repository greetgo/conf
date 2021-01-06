package kz.greetgo.conf.core;

import kz.greetgo.conf.error.CannotConvertToType;
import kz.greetgo.conf.test.util.ConfCallbackMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class ConfImplToCallbackTest_1 {

  interface Conf_paramStr {
    String paramStr();
  }

  @Test
  public void impl__paramStr() {

    ConfCallbackMap                   confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramStr> callback     = new ConfImplToCallback<>(Conf_paramStr.class, confCallback);

    //
    //
    Conf_paramStr impl = callback.impl();
    //
    //

    confCallback.prm("paramStr", "PVCg0jip5R");
    assertThat(impl.paramStr()).isEqualTo("PVCg0jip5R");
  }

  interface Conf_paramBool {
    boolean paramBool();
  }

  @Test
  public void impl__paramBool() {

    ConfCallbackMap                    confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramBool> callback     = new ConfImplToCallback<>(Conf_paramBool.class, confCallback);

    //
    //
    Conf_paramBool impl = callback.impl();
    //
    //

    confCallback.prm("paramBool", "true");
    assertThat(impl.paramBool()).isTrue();

    confCallback.prm("paramBool", "false");
    assertThat(impl.paramBool()).isFalse();

    confCallback.prm("paramBool", null);
    assertThat(impl.paramBool()).isFalse();
  }

  interface Conf_paramBoolBox {
    Boolean paramBoolBox();
  }

  @Test
  public void impl__paramBoolBox() {

    ConfCallbackMap                       confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramBoolBox> callback     = new ConfImplToCallback<>(Conf_paramBoolBox.class, confCallback);

    //
    //
    Conf_paramBoolBox impl = callback.impl();
    //
    //

    confCallback.prm("paramBoolBox", "true");
    assertThat(impl.paramBoolBox()).isTrue();

    confCallback.prm("paramBoolBox", "false");
    assertThat(impl.paramBoolBox()).isFalse();

    confCallback.prm("paramBoolBox", null);
    assertThat(impl.paramBoolBox()).isNull();
  }

  interface Conf_paramInt {
    int paramInt();
  }

  @Test
  public void impl__paramInt() {

    ConfCallbackMap                   confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramInt> callback     = new ConfImplToCallback<>(Conf_paramInt.class, confCallback);

    //
    //
    Conf_paramInt impl = callback.impl();
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

  interface Conf_paramIntBox {
    Integer paramIntBox();
  }

  @Test
  public void impl__paramIntBox() {

    ConfCallbackMap                      confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramIntBox> callback     = new ConfImplToCallback<>(Conf_paramIntBox.class, confCallback);

    //
    //
    Conf_paramIntBox impl = callback.impl();
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
  public void impl__call__equals() {

    ConfCallbackMap                      confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramIntBox> callback     = new ConfImplToCallback<>(Conf_paramIntBox.class, confCallback);

    //
    //
    Conf_paramIntBox impl = callback.impl();
    //
    //

    impl.equals(null);

  }

  @Test
  public void impl__call__hashCode() {

    ConfCallbackMap                      confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramIntBox> callback     = new ConfImplToCallback<>(Conf_paramIntBox.class, confCallback);

    //
    //
    Conf_paramIntBox impl = callback.impl();
    //
    //

    impl.hashCode();

  }

  interface Conf_paramFloat {
    float paramFloat();
  }

  @Test
  public void impl__paramFloat() {

    ConfCallbackMap                     confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramFloat> callback     = new ConfImplToCallback<>(Conf_paramFloat.class, confCallback);

    //
    //
    Conf_paramFloat impl = callback.impl();
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

  interface Conf_paramFloatBox {
    Float paramFloatBox();
  }

  @Test
  public void impl__paramFloatBox() {

    ConfCallbackMap                        confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramFloatBox> callback     = new ConfImplToCallback<>(Conf_paramFloatBox.class, confCallback);

    //
    //
    Conf_paramFloatBox impl = callback.impl();
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
  public void impl__paramFloat__CannotConvertToType() {

    ConfCallbackMap                     confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramFloat> callback     = new ConfImplToCallback<>(Conf_paramFloat.class, confCallback);

    //
    //
    Conf_paramFloat impl = callback.impl();
    //
    //

    confCallback.prm("paramFloat", "4h3b24b");
    impl.paramFloat();

  }

  @Test(expectedExceptions = CannotConvertToType.class)
  public void impl__paramDouble__CannotConvertToType() {

    ConfCallbackMap                      confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramDouble> callback     = new ConfImplToCallback<>(Conf_paramDouble.class, confCallback);

    //
    //
    Conf_paramDouble impl = callback.impl();
    //
    //

    confCallback.prm("paramDouble", "4h3b24b");
    impl.paramDouble();

  }

  interface Conf_paramDouble {
    double paramDouble();
  }

  @Test
  public void impl__paramDouble() {

    ConfCallbackMap                      confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramDouble> callback     = new ConfImplToCallback<>(Conf_paramDouble.class, confCallback);

    //
    //
    Conf_paramDouble impl = callback.impl();
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

  interface Conf_paramDoubleBox {
    Double paramDoubleBox();
  }

  @Test
  public void impl__paramDoubleBox() {

    ConfCallbackMap                         confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramDoubleBox> callback     = new ConfImplToCallback<>(Conf_paramDoubleBox.class, confCallback);

    //
    //
    Conf_paramDoubleBox impl = callback.impl();
    //
    //

    confCallback.prm("paramDoubleBox", "4325.456");
    assertThat(impl.paramDoubleBox()).isEqualTo(4325.456d);

    confCallback.prm("paramDoubleBox", "-6543.12");
    assertThat(impl.paramDoubleBox()).isEqualTo(-6543.12d);

    confCallback.prm("paramDoubleBox", null);
    assertThat(impl.paramDoubleBox()).isNull();

  }

  interface Conf_paramBigDecimal {
    BigDecimal paramBigDecimal();
  }

  private static BigDecimal bd(String s) {
    return new BigDecimal(s);
  }

  @Test
  public void impl__paramBigDecimal() {

    ConfCallbackMap                          confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramBigDecimal> callback     = new ConfImplToCallback<>(Conf_paramBigDecimal.class, confCallback);

    //
    //
    Conf_paramBigDecimal impl = callback.impl();
    //
    //

    confCallback.prm("paramBigDecimal", "4325.456");
    assertThat(impl.paramBigDecimal()).isEqualTo(bd("4325.456"));

    confCallback.prm("paramBigDecimal", "-6543.12");
    assertThat(impl.paramBigDecimal()).isEqualTo(bd("-6543.12"));

    confCallback.prm("paramBigDecimal", null);
    assertThat(impl.paramBigDecimal()).isEqualTo(BigDecimal.ZERO);
  }

  interface Conf_paramDate {
    Date paramDate();
  }

  private static Date dat(String s) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      return sdf.parse(s);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void impl__paramDate() {

    ConfCallbackMap                    confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramDate> callback     = new ConfImplToCallback<>(Conf_paramDate.class, confCallback);

    //
    //
    Conf_paramDate impl = callback.impl();
    //
    //


    confCallback.prm("paramDate", "2010-10-11 12:13:14");
    assertThat(impl.paramDate()).isEqualTo(dat("2010-10-11 12:13:14"));

    confCallback.prm("paramDate", "2010-10-13");
    assertThat(impl.paramDate()).isEqualTo(dat("2010-10-13 00:00:00"));

    confCallback.prm("paramDate", null);
    assertThat(impl.paramDate()).isNull();
  }

  public enum TestEnum {
    ELEMENT_001,
    ELEMENT_002,
    ELEMENT_003,
  }

  interface Conf_paramEnum {
    TestEnum paramEnum();
  }

  @Test
  public void impl__paramEnum() {

    ConfCallbackMap                    confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf_paramEnum> callback     = new ConfImplToCallback<>(Conf_paramEnum.class, confCallback);

    //
    //
    Conf_paramEnum impl = callback.impl();
    //
    //


    confCallback.prm("paramEnum", "ELEMENT_001");
    assertThat(impl.paramEnum()).isEqualTo(TestEnum.ELEMENT_001);

    confCallback.prm("paramEnum", "ELEMENT_003");
    assertThat(impl.paramEnum()).isEqualTo(TestEnum.ELEMENT_003);

    confCallback.prm("paramEnum", null);
    assertThat(impl.paramEnum()).isNull();

    confCallback.prm("paramEnum", "LEFT");
    assertThat(impl.paramEnum()).isNull();
  }


}
