package kz.greetgo.conf.core;

import kz.greetgo.conf.hot.FirstReadEnv;
import kz.greetgo.conf.test.util.ConfCallbackMap;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class ConfImplToCallbackTest_2 {

  interface Conf1 {

    String strParam();

    long longParam();

  }

  interface Conf2 {

    String paramStr();

    Conf1 conf1();

  }

  interface Conf3 {

    int intParam();

    List<Conf2> listConf2();

  }

  @Test
  public void impl__listConf2__paramStr__1() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf3> callback     = new ConfImplToCallback<>(Conf3.class, confCallback);

    //
    //
    Conf3 impl = callback.impl();
    //
    //

    confCallback.clear();
    confCallback.siz("listConf2", 4);
    confCallback.prm("listConf2.1.paramStr", "ddZNlI01vd");
    assertThat(impl.listConf2().get(1).paramStr()).isEqualTo("ddZNlI01vd");
  }

  @Test
  public void impl__listConf2__paramStr__2() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf3> callback     = new ConfImplToCallback<>(Conf3.class, confCallback);

    //
    //
    Conf3 impl = callback.impl();
    //
    //

    confCallback.clear();

    confCallback.siz("listConf2", 2);
    confCallback.prm("listConf2.1.paramStr", "nwd3SHhLfP");
    assertThat(impl.listConf2().get(1).paramStr()).isEqualTo("nwd3SHhLfP");

    confCallback.siz("listConf2", 4);
    confCallback.prm("listConf2.1.paramStr", "6HVgiCy1u3");
    confCallback.prm("listConf2.2.paramStr", "P0PT9lY5Hn");
    assertThat(impl.listConf2().get(1).paramStr()).isEqualTo("6HVgiCy1u3");
    assertThat(impl.listConf2().get(2).paramStr()).isEqualTo("P0PT9lY5Hn");
  }

  @Test
  public void impl__listConf2__paramStr__3() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf3> callback     = new ConfImplToCallback<>(Conf3.class, confCallback);

    //
    //
    Conf3 impl = callback.impl();
    //
    //

    confCallback.clear();

    confCallback.siz("listConf2", 7);
    confCallback.prm("listConf2.1.paramStr", "VWP7ddTI6R");
    assertThat(impl.listConf2().get(1).paramStr()).isEqualTo("VWP7ddTI6R");

    confCallback.siz("listConf2", 3);
    confCallback.prm("listConf2.1.paramStr", "cF0CN69B65");
    confCallback.prm("listConf2.2.paramStr", "rXu1rNtu6I");
    assertThat(impl.listConf2().get(1).paramStr()).isEqualTo("cF0CN69B65");
    assertThat(impl.listConf2().get(2).paramStr()).isEqualTo("rXu1rNtu6I");
  }

  @Test
  public void impl__conf1__strParam() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf2> callback     = new ConfImplToCallback<>(Conf2.class, confCallback);

    //
    //
    Conf2 impl = callback.impl();
    //
    //

    confCallback.clear();
    confCallback.prm("conf1.strParam", "l8pJ3a9U9b");
    assertThat(impl.conf1().strParam()).isEqualTo("l8pJ3a9U9b");
  }

  @Test
  public void impl__listConf2__conf1__longParam() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf3> callback     = new ConfImplToCallback<>(Conf3.class, confCallback);

    //
    //
    Conf3 impl = callback.impl();
    //
    //

    confCallback.clear();
    confCallback.siz("listConf2", 4);
    confCallback.prm("listConf2.1.conf1.longParam", "8214738275");
    assertThat(impl.listConf2().get(1).conf1().longParam()).isEqualTo(8214738275L);
  }

  interface Conf4 {
    @FirstReadEnv("STONE_E")
    String stone();
  }

  @Test
  public void impl_FirstReadEnv() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf4> callback     = new ConfImplToCallback<>(Conf4.class, confCallback);

    confCallback.env("STONE_E", "stone from env");

    //
    //
    Conf4 impl = callback.impl();
    //
    //

    String stone = impl.stone();

    assertThat(stone).isEqualTo("stone from env");

  }

  @Test
  public void impl_FirstReadEnv__NoEnv() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf4> callback     = new ConfImplToCallback<>(Conf4.class, confCallback);

    confCallback.env("STONE_E", "");
    confCallback.prm("stone", "stone from file");

    //
    //
    Conf4 impl = callback.impl();
    //
    //

    String stone = impl.stone();

    assertThat(stone).isEqualTo("stone from file");

  }

  public enum Enum5 {
    VAL_X, VAL_WOW, STATUS
  }

  interface Conf5 {
    @FirstReadEnv("SIN_US")
    Enum5 sinus();
  }

  @Test
  public void impl_FirstReadEnv__enum() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf5> callback     = new ConfImplToCallback<>(Conf5.class, confCallback);

    confCallback.env("SIN_US", "VAL_WOW");

    //
    //
    Conf5 impl = callback.impl();
    //
    //

    Enum5 sinus = impl.sinus();

    assertThat(sinus).isEqualTo(Enum5.VAL_WOW);

  }

  @Test
  public void impl_FirstReadEnv__enum__noEnv() {

    ConfCallbackMap           confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf5> callback     = new ConfImplToCallback<>(Conf5.class, confCallback);

    confCallback.env("SIN_US", "   ");
    confCallback.prm("sinus", "VAL_X");

    //
    //
    Conf5 impl = callback.impl();
    //
    //

    Enum5 sinus = impl.sinus();

    assertThat(sinus).isEqualTo(Enum5.VAL_X);

  }

}
