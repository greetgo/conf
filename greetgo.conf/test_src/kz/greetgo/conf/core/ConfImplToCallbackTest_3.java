package kz.greetgo.conf.core;

import kz.greetgo.conf.test.util.ConfCallbackMap;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class ConfImplToCallbackTest_3 {

  public static class Model {
    public String strField;
    public int    intField;
  }

  interface Conf {
    Model model();
  }

  @Test
  public void impl__model() {

    ConfCallbackMap          confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback     = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    Conf impl = callback.impl();
    //
    //

    confCallback.prm("model.strField", "BdhYZc0Xs2");
    confCallback.prm("model.intField", "54326");
    assertThat(impl.model().strField).isEqualTo("BdhYZc0Xs2");
    assertThat(impl.model().intField).isEqualTo(54326);
  }

}
