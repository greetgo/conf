package kz.greetgo.conf.core;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultListSize;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;
import kz.greetgo.conf.test.util.ConfCallbackMap;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ConfImplToCallbackTest_4 {

  @SuppressWarnings("unused")
  @Description("Description pfE65v3Js3\n1y5MJ2ct38y\n1ZbF50qRv1m")
  interface Conf1 {

    @Description("Description Yc2O8GznYK\n1U3N4RG8rZf")
    @DefaultStrValue("OlQD1fORSn")
    String param1();

    @Description("Description gO7aCqMz16\njUzfLJtzIU")
    @DefaultIntValue(54267)
    int param2();

  }

  @Test
  public void defaultContent__plain() {
    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf1> callback = new ConfImplToCallback<>(Conf1.class, confCallback);

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

    Map<String, String> valueMap = confContent.records.stream().filter(x -> x.key != null)
      .collect(toMap(x -> x.key, x -> x.value));

    assertThat(valueMap).contains(entry("param1", "OlQD1fORSn"));
    assertThat(valueMap).contains(entry("param2", "54267"));

  }

  @SuppressWarnings("unused")
  interface Conf2 {

    @Description("Description Hi1a0o4KLd")
    @DefaultListSize(3)
    @DefaultStrValue("ieE5RbQZiP")
    List<Long> coolParam();

  }

  @Test
  public void defaultContent__plainArray() {
    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf2> callback = new ConfImplToCallback<>(Conf2.class, confCallback);

    //
    //
    ConfContent confContent = callback.defaultContent();
    //
    //

    Map<String, String> commentMap = confContent.records.stream()
      .collect(toMap(x -> x.key, x -> String.join("~", x.comments)));

    assertThat(commentMap).contains(entry("coolParam.0", "Description Hi1a0o4KLd"));
    assertThat(commentMap).contains(entry("coolParam.1", ""));
    assertThat(commentMap).contains(entry("coolParam.2", ""));

    Map<String, String> valueMap = confContent.records.stream().filter(x -> x.key != null)
      .collect(toMap(x -> x.key, x -> x.value));

    assertThat(valueMap).contains(entry("coolParam.0", "ieE5RbQZiP"));
    assertThat(valueMap).contains(entry("coolParam.1", "ieE5RbQZiP"));
    assertThat(valueMap).contains(entry("coolParam.2", "ieE5RbQZiP"));

  }

  @SuppressWarnings("unused")
  @Description("Description of SubConf3")
  interface SubConf3 {

    @Description("Description 59l98wOg1i")
    @DefaultStrValue("pu1ovJFvhQ")
    String subParam();

  }

  @SuppressWarnings("unused")
  interface Conf3 {

    @Description("Description of confParam1")
    SubConf3 confParam1();

    @Description("Description of confParam2")
    SubConf3 confParam2();

  }

  @Test
  public void defaultContent__subConf() {
    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf3> callback = new ConfImplToCallback<>(Conf3.class, confCallback);

    //
    //
    ConfContent confContent = callback.defaultContent();
    //
    //

    {
      List<String> lines = new ArrayList<>();
      confContent.appendTo(lines);
      System.out.println(String.join("\n", lines));
    }

    Map<String, String> commentMap = confContent.records.stream()
      .collect(toMap(x -> x.key, x -> String.join("~", x.comments)));

    assertThat(commentMap).contains(entry("confParam1", "Description of SubConf3~~Description of confParam1"));
    assertThat(commentMap).contains(entry("confParam1.subParam", "Description 59l98wOg1i"));

    assertThat(commentMap).contains(entry("confParam2", "Description of SubConf3~~Description of confParam2"));
    assertThat(commentMap).contains(entry("confParam2.subParam", "Description 59l98wOg1i"));

    Map<String, String> valueMap = confContent.records.stream().filter(x -> x.key != null)
      .collect(toMap(x -> x.key, x -> "" + x.value));

    assertThat(valueMap).contains(entry("confParam1", "null"));
    assertThat(valueMap).contains(entry("confParam1.subParam", "pu1ovJFvhQ"));
    assertThat(valueMap).contains(entry("confParam2", "null"));
    assertThat(valueMap).contains(entry("confParam2.subParam", "pu1ovJFvhQ"));

  }

  @SuppressWarnings("unused")
  @Description("Description of SubModel4")
  public static class SubModel4 {

    @Description("Description of subParam1")
    @DefaultStrValue("pu1ovJFvhQ")
    public String subParam1;

    @Description("Description of subParam2")
    @DefaultStrValue("0Lb8prJi47")
    public String subParam2;

  }

  @SuppressWarnings("unused")
  @Description("Description of Conf4")
  interface Conf4 {

    @Description("Description of modelParam1")
    SubModel4 modelParam1();

    @Description("Description of modelParam2")
    SubModel4 modelParam2();

  }

  @Test
  public void defaultContent__subModel() {
    ConfCallbackMap confCallback = new ConfCallbackMap();
    ConfImplToCallback<Conf4> callback = new ConfImplToCallback<>(Conf4.class, confCallback);

    //
    //
    ConfContent confContent = callback.defaultContent();
    //
    //

    {
      List<String> lines = new ArrayList<>();
      confContent.appendTo(lines);
      System.out.println(String.join("\n", lines));
    }

    Map<String, String> commentMap = confContent.records.stream().filter(x -> x.key != null)
      .collect(toMap(x -> x.key, x -> String.join("~", x.comments)));

    assertThat(commentMap).contains(entry("modelParam1", "Description of SubModel4~~Description of modelParam1"));
    assertThat(commentMap).contains(entry("modelParam1.subParam1", "Description of subParam1"));
    assertThat(commentMap).contains(entry("modelParam1.subParam2", "Description of subParam2"));
    assertThat(commentMap).contains(entry("modelParam2", "Description of SubModel4~~Description of modelParam2"));
    assertThat(commentMap).contains(entry("modelParam2.subParam1", "Description of subParam1"));
    assertThat(commentMap).contains(entry("modelParam2.subParam2", "Description of subParam2"));

  }
}
