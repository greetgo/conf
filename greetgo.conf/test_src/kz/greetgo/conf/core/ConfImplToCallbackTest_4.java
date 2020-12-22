package kz.greetgo.conf.core;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;
import kz.greetgo.conf.test.util.ConfCallbackMap;
import org.testng.annotations.Test;

import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ConfImplToCallbackTest_4 {

  @SuppressWarnings("unused")
  @Description("Description pfE65v3Js3\n1y5MJ2ct38y\n1ZbF50qRv1m")
  interface Conf {

    @Description("Description Yc2O8GznYK\n1U3N4RG8rZf")
    @DefaultStrValue("OlQD1fORSn")
    String param1();

    @Description("Description gO7aCqMz16\njUzfLJtzIU")
    @DefaultIntValue(54267)
    int param2();

  }

  @Test
  public void defaultContent() {
    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf> callback = new ConfImplToCallback<>(Conf.class, confCallback);

    //
    //
    ConfContent confContent = callback.defaultContent();
    //
    //

    assertThat(String.join("~", confContent.records.get(0).comments))
      .isEqualTo("Description pfE65v3Js3~1y5MJ2ct38y~1ZbF50qRv1m");

    Map<String, String> commentMap = confContent.records.stream()
      .collect(toMap(x -> x.key, x -> String.join("~", x.comments)));

    assertThat(commentMap).contains(entry("param1", "Description Yc2O8GznYK~1U3N4RG8rZf"));
    assertThat(commentMap).contains(entry("param2", "Description gO7aCqMz16~jUzfLJtzIU"));

  }

}
