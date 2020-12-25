package kz.greetgo.conf.hot;

import org.assertj.core.data.MapEntry;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigDataLoaderTest {

  @DataProvider
  public Object[][] useDefaultDataProvider() {
    return new Object[][]{
      {false}, {true}
    };
  }

  @Test(dataProvider = "useDefaultDataProvider")
  public void loadConfigDataTo_int(boolean useDefault) {
    Map<String, Object> target = new HashMap<>();

    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "some", Object.class, "some", Collections.singletonList(
      ElementDefinition.newOne("field", int.class, 1300, "")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    if (!useDefault) configContent.saveConfigContent("some", "field=8156");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, new Date());
    //
    //

    assertThat(target).contains(MapEntry.entry("field", useDefault ? 1300 : 8156));
  }

  @Test(dataProvider = "useDefaultDataProvider")
  public void loadConfigDataTo_long(boolean useDefault) {
    Map<String, Object> target = new HashMap<>();

    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "some", Object.class, "some", Collections.singletonList(
      ElementDefinition.newOne("field", long.class, 17_000L, "")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    if (!useDefault) configContent.saveConfigContent("some", "field=65365424");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, new Date());
    //
    //

    assertThat(target).contains(MapEntry.entry("field", useDefault ? 17_000L : 65365424L));
  }

  @Test(dataProvider = "useDefaultDataProvider")
  public void loadConfigDataTo_str(boolean useDefault) {
    Map<String, Object> target = new HashMap<>();

    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "some", Object.class, "some", Collections.singletonList(
      ElementDefinition.newOne("field", String.class, "DEF_STR", "")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    if (!useDefault) configContent.saveConfigContent("some", "field=good value");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, new Date());
    //
    //

    assertThat(target).contains(MapEntry.entry("field", useDefault ? "DEF_STR" : "good value"));
  }

  @Test(dataProvider = "useDefaultDataProvider")
  public void loadConfigDataTo_bool_1(boolean useDefault) {
    Map<String, Object> target = new HashMap<>();

    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "some", Object.class, "some", Collections.singletonList(
      ElementDefinition.newOne("field", boolean.class, true, "")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    if (!useDefault) configContent.saveConfigContent("some", "field=off");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, new Date());
    //
    //

    assertThat(target).contains(MapEntry.entry("field", useDefault));
  }

  @Test(dataProvider = "useDefaultDataProvider")
  public void loadConfigDataTo_bool_2(boolean useDefault) {
    Map<String, Object> target = new HashMap<>();

    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "some", Object.class, "some", Collections.singletonList(
      ElementDefinition.newOne("field", boolean.class, false, "")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    if (!useDefault) configContent.saveConfigContent("some", "field=on");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, new Date());
    //
    //

    assertThat(target).contains(MapEntry.entry("field", !useDefault));
  }

  @Test(dataProvider = "useDefaultDataProvider")
  public void loadConfigDataTo_FieldClass(boolean useDefault) {
    Map<String, Object> target = new HashMap<>();

    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "some", Object.class, "some", Collections.singletonList(
      ElementDefinition.newOne("hello", FieldClass.class, null, "")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    if (!useDefault) configContent.saveConfigContent("some", "hello.field1=7654\nhello.field2=Good string");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, new Date());
    //
    //

    FieldClass def = new FieldClass();

    assertThat(target.get("hello")).isInstanceOf(FieldClass.class);
    FieldClass hello = (FieldClass) target.get("hello");

    assertThat(hello.field1).isEqualTo(useDefault ? def.field1 : 7654);
    assertThat(hello.field2).isEqualTo(useDefault ? def.field2 : "Good string");
  }

  @Test
  public void loadConfigDataTo_createsNew() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "hello/asd.txt", Object.class, "hello world\nsingle", Arrays.asList(
      ElementDefinition.newOne("intElement", int.class, 100, "int element description\nin two lines"),
      ElementDefinition.newOne("strElement", String.class, "asd", "str element description\nin two lines"),
      ElementDefinition.newOne("boolElement", boolean.class, true, "bool element description\nin two lines")
    ));
    ConfigStorageForTests configContent = new ConfigStorageForTests();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-01-01 12:09:11.345");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(3);
    assertThat(target).contains(MapEntry.entry("intElement", 100));
    assertThat(target).contains(MapEntry.entry("strElement", "asd"));
    assertThat(target).contains(MapEntry.entry("boolElement", true));

    String content = configContent.loadConfigContent("hello/asd.txt");
    assertThat(content).isEqualTo("\n" +
      "#\n" +
      "# Created at 2016-01-01 12:09:11.345\n" +
      "#\n" +
      "# hello world\n" +
      "# single\n" +
      "#\n" +
      "\n" +
      "# int element description\n" +
      "# in two lines\n" +
      "intElement=100\n" +
      "\n" +
      "# str element description\n" +
      "# in two lines\n" +
      "strElement=asd\n" +
      "\n" +
      "# bool element description\n" +
      "# in two lines\n" +
      "boolElement=true\n");
  }

  @Test
  public void loadConfigDataTo_appendMore() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "hello/asd.txt", Object.class, "hello world\nsingle", Collections.singletonList(
      ElementDefinition.newOne("intElement", int.class, 123, "int element description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.saveConfigContent("hello/asd.txt", "\nhello=wow\n");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-01-01 12:09:11.345");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(1);
    assertThat(target).contains(MapEntry.entry("intElement", 123));

    String content = configContent.loadConfigContent("hello/asd.txt");

    assertThat(content).isEqualTo("\n" +
      "hello=wow\n" +
      "\n" +
      "#\n" +
      "# Added at 2016-01-01 12:09:11.345\n" +
      "#\n" +
      "\n" +
      "# int element description\n" +
      "# in two lines\n" +
      "intElement=123\n");
  }

  @Test
  public void loadConfigDataTo_appendMore2() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "hello/asd.txt", Object.class, "hello world\nsingle", Arrays.asList(
      ElementDefinition.newOne("intElement1", int.class, 123, "int element 1 description\nin two lines"),
      ElementDefinition.newOne("intElement2", int.class, 567, "int element 2 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.saveConfigContent("hello/asd.txt", "\nintElement1=809\n");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-01-01 12:09:11.345");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(2);
    assertThat(target).contains(MapEntry.entry("intElement1", 809));
    assertThat(target).contains(MapEntry.entry("intElement2", 567));

    String content = configContent.loadConfigContent("hello/asd.txt");

    assertThat(content).isEqualTo("\n" +
      "intElement1=809\n" +
      "\n" +
      "#\n" +
      "# Added at 2016-01-01 12:09:11.345\n" +
      "#\n" +
      "\n" +
      "# int element 2 description\n" +
      "# in two lines\n" +
      "intElement2=567\n");
  }

  @Test
  public void loadConfigDataTo_appendMore3() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "hello/asd.txt", Object.class, "hello world\nsingle", Arrays.asList(
      ElementDefinition.newOne("intElement1", int.class, 876, "int element 1 description\nin two lines"),
      ElementDefinition.newOne("intElement2", int.class, 543, "int element 2 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.saveConfigContent("hello/asd.txt", "\nintElement1=xx809\n");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-02-01 13:09:17.111");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(2);
    assertThat(target).contains(MapEntry.entry("intElement1", 876));
    assertThat(target).contains(MapEntry.entry("intElement2", 543));

    String content = configContent.loadConfigContent("hello/asd.txt");

    assertThat(content).isEqualTo("\n" +
      "intElement1=xx809\n" +
      "\n" +
      "#\n" +
      "# Added at 2016-02-01 13:09:17.111\n" +
      "#\n" +
      "\n" +
      "# int element 2 description\n" +
      "# in two lines\n" +
      "intElement2=543\n");
  }

  @Test
  public void loadConfigDataTo_allExists() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "hello/asd.txt", Object.class, "hello world\nsingle", Arrays.asList(
      ElementDefinition.newOne("intElement1", int.class, 311, "int element 1 description\nin two lines"),
      ElementDefinition.newOne("intElement2", Integer.class, 544, "int element 2 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    String initialContent = "\nintElement1=777\nintElement2=888\n";
    configContent.saveConfigContent("hello/asd.txt", initialContent);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-02-01 13:09:17.111");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(2);
    assertThat(target).contains(MapEntry.entry("intElement1", 777));
    assertThat(target).contains(MapEntry.entry("intElement2", 888));

    String content = configContent.loadConfigContent("hello/asd.txt");

    assertThat(content).isEqualTo(initialContent);
  }

  @Test
  public void loadConfigDataTo_commentedIsExists() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "hello/asd.txt", Object.class, "hello world\nsingle", Collections.singletonList(
      ElementDefinition.newOne("intElement1", int.class, 311, "int element 1 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    String initialContent = "#intElement1=777";
    configContent.saveConfigContent("hello/asd.txt", initialContent);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-02-01 13:09:17.111");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(1);
    assertThat(target).contains(MapEntry.entry("intElement1", 311));

    String content = configContent.loadConfigContent("hello/asd.txt");

    assertThat(content).isEqualTo(initialContent);
  }

  @Test
  public void loadConfigDataTo_doubleCommentedIsExists() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      "hello/asd.txt", Object.class, "hello world\nsingle", Collections.singletonList(
      ElementDefinition.newOne("intElement1", int.class, 311, "int element 1 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.saveConfigContent("hello/asd.txt", "##intElement1=777");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-03-07 13:09:17.333");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(1);
    assertThat(target).contains(MapEntry.entry("intElement1", 311));

    String content = configContent.loadConfigContent("hello/asd.txt");
    assertThat(content).isEqualTo("##intElement1=777\n" +
      "\n" +
      "#\n" +
      "# Added at 2016-03-07 13:09:17.333\n" +
      "#\n" +
      "\n" +
      "# int element 1 description\n" +
      "# in two lines\n" +
      "intElement1=311\n");
  }

  public static class FieldClass {
    public int field1 = 123;

    public String field2 = "Hello";
  }

  @Test
  public void loadConfigDataTo_readAnyType() throws Exception {
    Map<String, Object> target = new HashMap<>();

    String location = "hello/wow.txt";

    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
      location, Object.class, "title", Collections.singletonList(
      ElementDefinition.newOne("element", FieldClass.class, null, "some info")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.saveConfigContent(location, "#left content");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-03-07 13:09:34.112");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(1);
    assertThat(target.get("element")).isInstanceOf(FieldClass.class);
    FieldClass element = (FieldClass) target.get("element");
    assertThat(element.field1).isEqualTo(123);
    assertThat(element.field2).isEqualTo("Hello");

    String content = configContent.loadConfigContent(location);
    assertThat(content).isEqualTo("#left content\n" +
      "\n" +
      "#\n" +
      "# Added at 2016-03-07 13:09:34.112\n" +
      "#\n" +
      "\n" +
      "# some info\n" +
      "element.field1=123\n" +
      "\n" +
      "# some info\n" +
      "element.field2=Hello\n");
  }
}
