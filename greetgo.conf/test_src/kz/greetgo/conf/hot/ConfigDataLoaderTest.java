package kz.greetgo.conf.hot;

import org.fest.assertions.data.MapEntry;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;

public class ConfigDataLoaderTest {

  @Test
  public void loadConfigDataTo_createsNew() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
        "hello/asd.txt", "hello world\nsingle", Arrays.asList(
        new ElementDefinition("intElement", int.class, 100, "int element description\nin two lines"),
        new ElementDefinition("strElement", String.class, "asd", "str element description\nin two lines"),
        new ElementDefinition("boolElement", boolean.class, true, "bool element description\nin two lines")
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

    String content = configContent.contentMap.get("hello/asd.txt");
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
        "hello/asd.txt", "hello world\nsingle", Collections.singletonList(
        new ElementDefinition("intElement", int.class, 123, "int element description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.contentMap.put("hello/asd.txt", "\nhello=wow\n");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-01-01 12:09:11.345");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(1);
    assertThat(target).contains(MapEntry.entry("intElement", 123));

    String content = configContent.contentMap.get("hello/asd.txt");

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
        "hello/asd.txt", "hello world\nsingle", Arrays.asList(
        new ElementDefinition("intElement1", int.class, 123, "int element 1 description\nin two lines"),
        new ElementDefinition("intElement2", int.class, 567, "int element 2 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.contentMap.put("hello/asd.txt", "\nintElement1=809\n");

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

    String content = configContent.contentMap.get("hello/asd.txt");

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
        "hello/asd.txt", "hello world\nsingle", Arrays.asList(
        new ElementDefinition("intElement1", int.class, 876, "int element 1 description\nin two lines"),
        new ElementDefinition("intElement2", int.class, 543, "int element 2 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.contentMap.put("hello/asd.txt", "\nintElement1=xx809\n");

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

    String content = configContent.contentMap.get("hello/asd.txt");

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
        "hello/asd.txt", "hello world\nsingle", Arrays.asList(
        new ElementDefinition("intElement1", int.class, 311, "int element 1 description\nin two lines"),
        new ElementDefinition("intElement2", Integer.class, 544, "int element 2 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    String initialContent = "\nintElement1=777\nintElement2=888\n";
    configContent.contentMap.put("hello/asd.txt", initialContent);

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

    String content = configContent.contentMap.get("hello/asd.txt");

    assertThat(content).isEqualTo(initialContent);
  }

  @Test
  public void loadConfigDataTo_commentedIsExists() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
        "hello/asd.txt", "hello world\nsingle", Collections.singletonList(
        new ElementDefinition("intElement1", int.class, 311, "int element 1 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    String initialContent = "#intElement1=777";
    configContent.contentMap.put("hello/asd.txt", initialContent);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-02-01 13:09:17.111");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(1);
    assertThat(target).contains(MapEntry.entry("intElement1", 311));

    String content = configContent.contentMap.get("hello/asd.txt");

    assertThat(content).isEqualTo(initialContent);
  }

  @Test
  public void loadConfigDataTo_doubleCommentedIsExists() throws Exception {
    Map<String, Object> target = new HashMap<>();
    HotConfigDefinitionModel configDefinition = new HotConfigDefinitionModel(
        "hello/asd.txt", "hello world\nsingle", Collections.singletonList(
        new ElementDefinition("intElement1", int.class, 311, "int element 1 description\nin two lines")
    ));

    ConfigStorageForTests configContent = new ConfigStorageForTests();
    configContent.contentMap.put("hello/asd.txt", "##intElement1=777");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    Date now = sdf.parse("2016-03-07 13:09:17.333");

    //
    //
    ConfigDataLoader.loadConfigDataTo(target, configDefinition, configContent, now);
    //
    //

    assertThat(target).hasSize(1);
    assertThat(target).contains(MapEntry.entry("intElement1", 311));

    String content = configContent.contentMap.get("hello/asd.txt");
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
}