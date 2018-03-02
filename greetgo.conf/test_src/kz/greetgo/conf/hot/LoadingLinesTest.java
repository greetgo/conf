package kz.greetgo.conf.hot;

import org.fest.assertions.data.MapEntry;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class LoadingLinesTest {
  @Test
  public void readExistingIntField() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Hello world\nand more");
    ll.setContentExists(true);

    ll.putDefinition(new ElementDefinition("field1", int.class, 10, "первая строка"));

    ll.readStorageLine("    field1 = 799");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target).contains(MapEntry.entry("field1", 799));

    assertThat(ll.content()).isEqualTo("    field1 = 799\n");
  }

  @Test
  public void readExistingCommentedIntField() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Hello world\nand more");
    ll.setContentExists(true);

    ll.putDefinition(new ElementDefinition("field1", int.class, 10, "первая строка"));

    ll.readStorageLine("  #  field1 = 799");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(ll.content()).isEqualTo("  #  field1 = 799\n");

    assertThat(target).contains(MapEntry.entry("field1", 10));
  }

  @Test
  public void newContentIntField() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Hello world\nand more");
    ll.setContentExists(false);

    ll.putDefinition(new ElementDefinition("field1", int.class, 37, "первая строка"));

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(ll.content()).isEqualTo("\n" +
      "#\n" +
      "# Created at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "# Hello world\n" +
      "# and more\n" +
      "#\n" +
      "\n" +
      "# первая строка\n" +
      "field1=37\n");

    assertThat(target).contains(MapEntry.entry("field1", 37));
  }

  public static class ConfigElementClass {
    public int subField1 = 796;
    public String subField2 = "Привет Харальд";
  }

  @Test
  public void readExistingClassField() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Hello world\nand more");
    ll.setContentExists(true);

    ll.putDefinition(new ElementDefinition("topField1", ConfigElementClass.class, 10, "Описание топ-поля"));

    ll.readStorageLine("    topField1.subField1 = 2008");
    ll.readStorageLine("    topField1.subField2 = Понедельник начинается в  субботу   ");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);


    assertThat(target.get("topField1")).isInstanceOf(ConfigElementClass.class);

    ConfigElementClass element = (ConfigElementClass) target.get("topField1");

    assertThat(element.subField1).isEqualTo(2008);
    assertThat(element.subField2).isEqualTo("Понедельник начинается в  субботу");

    assertThat(ll.content()).isEqualTo("    topField1.subField1 = 2008\n" +
      "    topField1.subField2 = Понедельник начинается в  субботу   \n");
  }

  @Test
  public void readHalfExistingClassField() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Hello world\nand more");
    ll.setContentExists(true);

    ll.putDefinition(new ElementDefinition("topField1", ConfigElementClass.class, 10, "Описание топ-поля"));

    ll.readStorageLine("    topField1.subField1 = 2008");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topField1")).isInstanceOf(ConfigElementClass.class);

    ConfigElementClass element = (ConfigElementClass) target.get("topField1");

    assertThat(element.subField1).isEqualTo(2008);
    assertThat(element.subField2).isEqualTo("Привет Харальд");

    assertThat(ll.content()).isEqualTo("    topField1.subField1 = 2008\n" +
      "\n" +
      "#\n" +
      "# Added at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "\n" +
      "# Описание топ-поля\n" +
      "topField1.subField2=Привет Харальд\n");
  }

  @Test
  public void readNewClassField() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Это заголовок\nвсего файла");
    ll.setContentExists(false);

    ll.putDefinition(new ElementDefinition("topField1", ConfigElementClass.class, 10, "Описание топ-поля"));

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topField1")).isInstanceOf(ConfigElementClass.class);

    ConfigElementClass element = (ConfigElementClass) target.get("topField1");

    assertThat(element.subField1).isEqualTo(796);
    assertThat(element.subField2).isEqualTo("Привет Харальд");

    assertThat(ll.content()).isEqualTo("\n" +
      "#\n" +
      "# Created at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "# Это заголовок\n" +
      "# всего файла\n" +
      "#\n" +
      "\n" +
      "# Описание топ-поля\n" +
      "topField1.subField1=796\n" +
      "\n" +
      "# Описание топ-поля\n" +
      "topField1.subField2=Привет Харальд\n");
  }
}