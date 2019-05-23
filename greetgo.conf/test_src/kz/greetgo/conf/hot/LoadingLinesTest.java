package kz.greetgo.conf.hot;


import org.assertj.core.data.MapEntry;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kz.greetgo.conf.hot.HotConfigConstants.COUNT_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;

public class LoadingLinesTest {
  @Test
  public void readExistingIntField() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Hello world\nand more");
    ll.setContentExists(true);

    ll.putDefinition(ElementDefinition.newOne("field1", int.class, 10, "первая строка"));

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

    ll.putDefinition(ElementDefinition.newOne("field1", int.class, 10, "первая строка"));

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

    ll.putDefinition(ElementDefinition.newOne("field1", int.class, 37, "первая строка"));

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

    ll.putDefinition(ElementDefinition.newOne("topField1", ConfigElementClass.class, 10, "Описание топ-поля"));

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

    ll.putDefinition(ElementDefinition.newOne("topField1", ConfigElementClass.class, 10, "Описание топ-поля"));

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

    ll.putDefinition(ElementDefinition.newOne("topField1", ConfigElementClass.class, 10, "Описание топ-поля"));

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

  @Test
  public void testNullStringField() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, null);
    ll.setContentExists(false);

    ll.putDefinition(ElementDefinition.newOne("field", String.class, null, null));

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target).contains(MapEntry.entry("field", null));

    assertThat(ll.content()).isEqualTo("\n" +
      "#\n" +
      "# Created at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "\n" +
      "field=\n");
  }

  @Test
  public void testNullIntBoxedField() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, null);
    ll.setContentExists(false);

    ll.putDefinition(ElementDefinition.newOne("field", Integer.class, null, null));

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target).contains(MapEntry.entry("field", null));

    assertThat(ll.content()).isEqualTo("\n" +
      "#\n" +
      "# Created at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "\n" +
      "field=\n");
  }

  @Description("class about\nmore class about")
  public static class ClassWithDescriptions {
    @SuppressWarnings("unused")
    @Description("about field1\nabout field1 more 1")
    public String field1;

    private String field2;

    @SuppressWarnings("unused")
    @Description("about field2 from getter\nabout field2 more 2")
    public String getField2() {
      return field2;
    }

    @SuppressWarnings("unused")
    @Description("about field2 from setter\nabout field2 more 3")
    public void setField2(String field2) {
      this.field2 = field2;
    }

    public String field3;

    @SuppressWarnings("unused")
    @Description("about field3 from setter\nabout field3 more 4")
    public void setField3(String field3) {
      this.field3 = field3;
    }

    @Description("about field4 from field\nabout field4 more 5")
    public String field4;

    @SuppressWarnings("unused")
    @Description("about field4 from setter\nabout field4 more 6")
    public void setField4(String field4) {
      this.field4 = field4;
    }
  }

  @Test
  public void testClassFieldDescription() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Это заголовок\nвсего файла");
    ll.setContentExists(false);

    ll.putDefinition(ElementDefinition.newOne("topField1", ClassWithDescriptions.class, 10, "Описание топ-поля 1\nещё одна строка описания"));
    ll.putDefinition(ElementDefinition.newOne("topField2", ClassWithDescriptions.class, 10, "Описание топ-поля 2\nещё одна строка описания"));

    assertThat(ll.configLineMap.get("topField1.field1").description()).isEqualTo(
      "Описание топ-поля 1\n" +
        "ещё одна строка описания\n" +
        "class about\n" +
        "more class about\n" +
        "about field1\n" +
        "about field1 more 1"
    );

    assertThat(ll.configLineMap.get("topField1.field2").description()).isEqualTo(
      "Описание топ-поля 1\n" +
        "ещё одна строка описания\n" +
        "class about\n" +
        "more class about\n" +
        "about field2 from getter\n" +
        "about field2 more 2"
    );

    assertThat(ll.configLineMap.get("topField1.field3").description()).isEqualTo(
      "Описание топ-поля 1\n" +
        "ещё одна строка описания\n" +
        "class about\n" +
        "more class about\n" +
        "about field3 from setter\n" +
        "about field3 more 4"
    );

    assertThat(ll.configLineMap.get("topField1.field4").description()).isEqualTo(
      "Описание топ-поля 1\n" +
        "ещё одна строка описания\n" +
        "class about\n" +
        "more class about\n" +
        "about field4 from field\n" +
        "about field4 more 5"
    );

    assertThat(ll.configLineMap.get("topField2.field1").description()).isEqualTo(
      "Описание топ-поля 2\n" +
        "ещё одна строка описания\n" +
        "class about\n" +
        "more class about\n" +
        "about field1\n" +
        "about field1 more 1"
    );

    assertThat(ll.configLineMap.get("topField2.field2").description()).isEqualTo(
      "Описание топ-поля 2\n" +
        "ещё одна строка описания\n" +
        "class about\n" +
        "more class about\n" +
        "about field2 from getter\n" +
        "about field2 more 2"
    );

    assertThat(ll.configLineMap.get("topField2.field3").description()).isEqualTo(
      "Описание топ-поля 2\n" +
        "ещё одна строка описания\n" +
        "class about\n" +
        "more class about\n" +
        "about field3 from setter\n" +
        "about field3 more 4"
    );

    assertThat(ll.configLineMap.get("topField2.field4").description()).isEqualTo(
      "Описание топ-поля 2\n" +
        "ещё одна строка описания\n" +
        "class about\n" +
        "more class about\n" +
        "about field4 from field\n" +
        "about field4 more 5"
    );

  }

  @Test
  public void testCutEmptyLinesAtEndOfFile() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, null);
    ll.setContentExists(true);

    ll.putDefinition(ElementDefinition.newOne("field1", int.class, 10, null));
    ll.putDefinition(ElementDefinition.newOne("field2", int.class, 20, null));
    ll.putDefinition(ElementDefinition.newOne("field3", int.class, 30, null));

    ll.readStorageLine("field1 = 100");
    ll.readStorageLine("    ");
    ll.readStorageLine(" field2 = 200");
    ll.readStorageLine("   ");
    ll.readStorageLine("    ");
    ll.readStorageLine("     ");
    ll.readStorageLine("      ");
    ll.readStorageLine("   \t  ");
    ll.readStorageLine("        ");
    ll.readStorageLine("         ");
    ll.readStorageLine("          ");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(ll.content()).isEqualTo("field1 = 100\n" +
      "    \n" +
      " field2 = 200\n" +
      "\n" +
      "#\n" +
      "# Added at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "\n" +
      "field3=30\n");
  }

  public static class ForList {
    public int field1 = 333;
    public int field2 = 444;

    @Override
    public String toString() {
      return "ForList{" +
        "field1=" + field1 +
        ", field2=" + field2 +
        '}';
    }
  }

  @Test
  public void testClassList_halfExists() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Описание поля");
    ll.setContentExists(true);

    ll.putDefinition(ElementDefinition.newList("topFieldA", ForList.class, null, "описание списка A"));
    ll.putDefinition(ElementDefinition.newList("topFieldB", ForList.class, null, "описание списка B"));

    ll.readStorageLine("topFieldA." + COUNT_SUFFIX + " = 3");
    ll.readStorageLine("topFieldA.0.field1 = 100");
    ll.readStorageLine("topFieldA.0.field2 = 200");
    ll.readStorageLine("topFieldA.1.field1 = 1000");
    ll.readStorageLine("topFieldA.1.field2 = 2000");

    ll.readStorageLine("topFieldB." + COUNT_SUFFIX + " = 2");
    ll.readStorageLine("topFieldB.0.field1 = 1700");
    ll.readStorageLine("topFieldB.0.field2 = 2700");
    ll.readStorageLine("topFieldB.1.field1 = 17000");
    ll.readStorageLine("topFieldB.1.field2 = 27000");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topFieldA")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topFieldA"))).hasSize(3);
    assertThat(((List) target.get("topFieldA")).get(0)).isInstanceOf(ForList.class);
    assertThat(((List) target.get("topFieldA")).get(1)).isInstanceOf(ForList.class);
    assertThat(((List) target.get("topFieldA")).get(2)).isInstanceOf(ForList.class);
    //noinspection unchecked
    List<ForList> topFieldA = (List<ForList>) target.get("topFieldA");
    assertThat(topFieldA.get(0).field1).isEqualTo(100);
    assertThat(topFieldA.get(0).field2).isEqualTo(200);
    assertThat(topFieldA.get(1).field1).isEqualTo(1000);
    assertThat(topFieldA.get(1).field2).isEqualTo(2000);
    assertThat(topFieldA.get(2).field1).isEqualTo(333);
    assertThat(topFieldA.get(2).field2).isEqualTo(444);

    assertThat(target.get("topFieldB")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topFieldB"))).hasSize(2);
    assertThat(((List) target.get("topFieldB")).get(0)).isInstanceOf(ForList.class);
    assertThat(((List) target.get("topFieldB")).get(1)).isInstanceOf(ForList.class);
    //noinspection unchecked
    List<ForList> topFieldB = (List<ForList>) target.get("topFieldB");
    assertThat(topFieldB.get(0).field1).isEqualTo(1700);
    assertThat(topFieldB.get(0).field2).isEqualTo(2700);
    assertThat(topFieldB.get(1).field1).isEqualTo(17000);
    assertThat(topFieldB.get(1).field2).isEqualTo(27000);

    assertThat(ll.configLineMap.get("topFieldA.0.field1").description()).isEqualTo("описание списка A");

    assertThat(ll.configLineMap.get("topFieldB.1.field2").description()).isEqualTo("описание списка B");

    assertThat(ll.content()).isEqualTo("topFieldA.listElementsCount = 3\n" +
      "topFieldA.0.field1 = 100\n" +
      "topFieldA.0.field2 = 200\n" +
      "topFieldA.1.field1 = 1000\n" +
      "topFieldA.1.field2 = 2000\n" +
      "topFieldB.listElementsCount = 2\n" +
      "topFieldB.0.field1 = 1700\n" +
      "topFieldB.0.field2 = 2700\n" +
      "topFieldB.1.field1 = 17000\n" +
      "topFieldB.1.field2 = 27000\n" +
      "\n" +
      "#\n" +
      "# Added at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "\n" +
      "# описание списка A\n" +
      "topFieldA.2.field1=333\n" +
      "\n" +
      "# описание списка A\n" +
      "topFieldA.2.field2=444\n");
  }

  @Test
  public void testClassList_new() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Описание поля");
    ll.setContentExists(false);

    ll.putDefinition(ElementDefinition.newList("topField", ForList.class, null, "описание списка"));

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topField")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topField"))).hasSize(1);
    assertThat(((List) target.get("topField")).get(0)).isInstanceOf(ForList.class);
    //noinspection unchecked
    List<ForList> topField = (List<ForList>) target.get("topField");
    assertThat(topField.get(0).field1).isEqualTo(333);
    assertThat(topField.get(0).field2).isEqualTo(444);

    assertThat(ll.configLineMap).containsKey("topField." + COUNT_SUFFIX);

    assertThat(ll.content()).isEqualTo("\n" +
      "#\n" +
      "# Created at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "# Описание поля\n" +
      "#\n" +
      "\n" +
      "# Количество элементов в topField\n" +
      "topField.listElementsCount=1\n" +
      "\n" +
      "# описание списка\n" +
      "topField.0.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.0.field2=444\n");
  }


  @Test
  public void testPrimitiveList_halfExists() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Описание поля");
    ll.setContentExists(true);

    ll.putDefinition(ElementDefinition.newList("topFieldA", int.class, 333, "описание A"));
    ll.putDefinition(ElementDefinition.newList("topFieldB", int.class, 444, "описание B"));

    ll.readStorageLine("topFieldA." + COUNT_SUFFIX + " = 3");
    ll.readStorageLine("topFieldA.0 = 100");
    ll.readStorageLine("topFieldA.1 = 1000");

    ll.readStorageLine("topFieldB." + COUNT_SUFFIX + " = 2");
    ll.readStorageLine("topFieldB.0 = 1700");
    ll.readStorageLine("topFieldB.1 = 17000");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topFieldA")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topFieldA"))).hasSize(3);
    assertThat(((List) target.get("topFieldA")).get(0)).isInstanceOf(Integer.class);
    assertThat(((List) target.get("topFieldA")).get(1)).isInstanceOf(Integer.class);
    assertThat(((List) target.get("topFieldA")).get(2)).isInstanceOf(Integer.class);
    //noinspection unchecked
    List<Integer> topFieldA = (List<Integer>) target.get("topFieldA");
    assertThat(topFieldA.get(0)).isEqualTo(100);
    assertThat(topFieldA.get(1)).isEqualTo(1000);
    assertThat(topFieldA.get(2)).isEqualTo(333);

    assertThat(target.get("topFieldB")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topFieldB"))).hasSize(2);
    assertThat(((List) target.get("topFieldB")).get(0)).isInstanceOf(Integer.class);
    assertThat(((List) target.get("topFieldB")).get(1)).isInstanceOf(Integer.class);
    //noinspection unchecked
    List<Integer> topFieldB = (List<Integer>) target.get("topFieldB");
    assertThat(topFieldB.get(0)).isEqualTo(1700);
    assertThat(topFieldB.get(1)).isEqualTo(17000);

    assertThat(ll.configLineMap.get("topFieldA.0").description()).isEqualTo("описание A");

    assertThat(ll.configLineMap.get("topFieldB.1").description()).isEqualTo("описание B");

    assertThat(ll.content()).isEqualTo("" +
      "topFieldA.listElementsCount = 3\n" +
      "topFieldA.0 = 100\n" +
      "topFieldA.1 = 1000\n" +
      "topFieldB.listElementsCount = 2\n" +
      "topFieldB.0 = 1700\n" +
      "topFieldB.1 = 17000\n" +
      "\n" +
      "#\n" +
      "# Added at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "\n" +
      "# описание A\n" +
      "topFieldA.2=333\n");
  }

  @Test
  public void testPrimitiveList_new() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Описание поля");
    ll.setContentExists(false);

    ll.putDefinition(ElementDefinition.newList("topField", int.class, 777, "описание списка"));

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topField")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topField"))).hasSize(1);
    assertThat(((List) target.get("topField")).get(0)).isInstanceOf(Integer.class);
    //noinspection unchecked
    List<Integer> topField = (List<Integer>) target.get("topField");
    assertThat(topField.get(0)).isEqualTo(777);

    assertThat(ll.configLineMap).containsKey("topField." + COUNT_SUFFIX);

    assertThat(ll.content()).isEqualTo("\n" +
      "#\n" +
      "# Created at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "# Описание поля\n" +
      "#\n" +
      "\n" +
      "# Количество элементов в массиве topField\n" +
      "topField.listElementsCount=1\n" +
      "\n" +
      "# описание списка\n" +
      "topField.0=777\n");
  }

  @Test
  public void testPrimitiveList_newFile_DefaultListSize() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Описание поля");
    ll.setContentExists(false);

    ll.putDefinition(ElementDefinition.create("topField", int.class, 777, "описание списка", 7));

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topField")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topField"))).hasSize(7);
    assertThat(((List) target.get("topField")).get(0)).isInstanceOf(Integer.class);
    //noinspection unchecked
    List<Integer> topField = (List<Integer>) target.get("topField");
    assertThat(topField.get(0)).isEqualTo(777);
    assertThat(topField.get(1)).isEqualTo(777);
    assertThat(topField.get(2)).isEqualTo(777);
    assertThat(topField.get(3)).isEqualTo(777);
    assertThat(topField.get(4)).isEqualTo(777);
    assertThat(topField.get(5)).isEqualTo(777);
    assertThat(topField.get(6)).isEqualTo(777);

    assertThat(ll.configLineMap).containsKey("topField." + COUNT_SUFFIX);

    assertThat(ll.content()).isEqualTo("\n" +
      "#\n" +
      "# Created at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "# Описание поля\n" +
      "#\n" +
      "\n" +
      "# Количество элементов в массиве topField\n" +
      "topField.listElementsCount=7\n" +
      "\n" +
      "# описание списка\n" +
      "topField.0=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.1=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.2=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.3=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.4=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.5=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.6=777\n");
  }

  @Test
  public void testPrimitiveList_exists_DefaultListSize() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Описание поля");
    ll.setContentExists(true);

    ll.putDefinition(ElementDefinition.create("topField", int.class, 777, "описание списка", 7));

    //ll.readStorageLine("topField." + COUNT_SUFFIX + " = 3");
    ll.readStorageLine("topField.0 = 100");
    ll.readStorageLine("topField.3 = 900");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topField")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topField"))).hasSize(7);
    assertThat(((List) target.get("topField")).get(0)).isInstanceOf(Integer.class);
    //noinspection unchecked
    List<Integer> topField = (List<Integer>) target.get("topField");
    assertThat(topField.get(0)).isEqualTo(100);
    assertThat(topField.get(1)).isEqualTo(777);
    assertThat(topField.get(2)).isEqualTo(777);
    assertThat(topField.get(3)).isEqualTo(900);
    assertThat(topField.get(4)).isEqualTo(777);
    assertThat(topField.get(5)).isEqualTo(777);
    assertThat(topField.get(6)).isEqualTo(777);

    assertThat(ll.configLineMap).containsKey("topField." + COUNT_SUFFIX);

    assertThat(ll.content()).isEqualTo("topField.0 = 100\n" +
      "topField.3 = 900\n" +
      "\n" +
      "#\n" +
      "# Added at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "\n" +
      "# Количество элементов в массиве topField\n" +
      "topField.listElementsCount=7\n" +
      "\n" +
      "# описание списка\n" +
      "topField.1=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.2=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.4=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.5=777\n" +
      "\n" +
      "# описание списка\n" +
      "topField.6=777\n");
  }

  @Test
  public void testClassList_newFile_DefaultListSize() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Описание поля");
    ll.setContentExists(false);

    ll.putDefinition(ElementDefinition.create("topField", ForList.class, null, "описание списка", 5));

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topField")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topField"))).hasSize(5);
    assertThat(((List) target.get("topField")).get(0)).isInstanceOf(ForList.class);
    //noinspection unchecked
    List<ForList> topField = (List<ForList>) target.get("topField");
    assertThat(topField.get(0).field1).isEqualTo(333);
    assertThat(topField.get(1).field1).isEqualTo(333);
    assertThat(topField.get(2).field1).isEqualTo(333);
    assertThat(topField.get(3).field1).isEqualTo(333);
    assertThat(topField.get(4).field1).isEqualTo(333);

    assertThat(ll.configLineMap).containsKey("topField." + COUNT_SUFFIX);

    assertThat(ll.content()).isEqualTo("\n" +
      "#\n" +
      "# Created at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "# Описание поля\n" +
      "#\n" +
      "\n" +
      "# Количество элементов в topField\n" +
      "topField.listElementsCount=5\n" +
      "\n" +
      "# описание списка\n" +
      "topField.0.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.0.field2=444\n" +
      "\n" +
      "# описание списка\n" +
      "topField.1.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.1.field2=444\n" +
      "\n" +
      "# описание списка\n" +
      "topField.2.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.2.field2=444\n" +
      "\n" +
      "# описание списка\n" +
      "topField.3.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.3.field2=444\n" +
      "\n" +
      "# описание списка\n" +
      "topField.4.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.4.field2=444\n");
  }

  @Test
  public void testClassList_exists_DefaultListSize() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date now = sdf.parse("2011-07-16 11:12:53.233");

    LoadingLines ll = new LoadingLines(now, "Описание поля");
    ll.setContentExists(true);

    ll.putDefinition(ElementDefinition.create("topField", ForList.class, null, "описание списка", 5));

    //ll.readStorageLine("topField." + COUNT_SUFFIX + " = 3");
    ll.readStorageLine("topField.0.field1 = 100");
    ll.readStorageLine("topField.3.field1 = 900");

    Map<String, Object> target = new HashMap<>();
    ll.saveTo(target);

    assertThat(target.get("topField")).isInstanceOf(List.class);
    //noinspection unchecked
    assertThat(((List) target.get("topField"))).hasSize(5);
    assertThat(((List) target.get("topField")).get(0)).isInstanceOf(ForList.class);
    //noinspection unchecked
    List<ForList> topField = (List<ForList>) target.get("topField");
    assertThat(topField.get(0).field1).isEqualTo(100);
    assertThat(topField.get(1).field1).isEqualTo(333);
    assertThat(topField.get(2).field1).isEqualTo(333);
    assertThat(topField.get(3).field1).isEqualTo(900);
    assertThat(topField.get(4).field1).isEqualTo(333);

    assertThat(ll.configLineMap).containsKey("topField." + COUNT_SUFFIX);

    assertThat(ll.content()).isEqualTo("topField.0.field1 = 100\n" +
      "topField.3.field1 = 900\n" +
      "\n" +
      "#\n" +
      "# Added at 2011-07-16 11:12:53.233\n" +
      "#\n" +
      "\n" +
      "# Количество элементов в topField\n" +
      "topField.listElementsCount=5\n" +
      "\n" +
      "# описание списка\n" +
      "topField.0.field2=444\n" +
      "\n" +
      "# описание списка\n" +
      "topField.1.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.1.field2=444\n" +
      "\n" +
      "# описание списка\n" +
      "topField.2.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.2.field2=444\n" +
      "\n" +
      "# описание списка\n" +
      "topField.3.field2=444\n" +
      "\n" +
      "# описание списка\n" +
      "topField.4.field1=333\n" +
      "\n" +
      "# описание списка\n" +
      "topField.4.field2=444\n");
  }
}
