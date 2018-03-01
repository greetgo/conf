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

    assertThat(ll.content()).isEqualTo("    field1 = 799");

    assertThat(target).contains(MapEntry.entry("field1", 799));
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

    assertThat(ll.content()).isEqualTo("  #  field1 = 799");

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
      "field1=37");

    assertThat(target).contains(MapEntry.entry("field1", 37));
  }


}