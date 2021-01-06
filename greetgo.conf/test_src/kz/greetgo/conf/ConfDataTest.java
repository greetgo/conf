package kz.greetgo.conf;

import kz.greetgo.conf.error.NoValue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public class ConfDataTest {
  @Test
  public void parseToPair_001() {
    String[] pair = ConfData.parseToPair("");
    assertThat(pair).isNull();
  }

  @Test
  public void parseToPair_002() throws Exception {
    String[] pair = ConfData.parseToPair("  asd  = qwerty   ");
    assertThat(pair).hasSize(2);
    assertThat(pair[0]).isEqualTo("asd");
    assertThat(pair[1]).isEqualTo("qwerty");
  }

  @Test
  public void parseToPair_002_1() throws Exception {
    String[] pair = ConfData.parseToPair("  asd  = qwerty  : wow     ");
    assertThat(pair).hasSize(2);
    assertThat(pair[0]).isEqualTo("asd");
    assertThat(pair[1]).isEqualTo("qwerty  : wow");
  }

  @Test
  public void parseToPair_003() throws Exception {
    String[] pair = ConfData.parseToPair("  asd  : qwerty   ");
    assertThat(pair).hasSize(2);
    assertThat(pair[0]).isEqualTo("asd");
    assertThat(pair[1]).isEqualTo(" qwerty   ");
  }

  @Test
  public void parseToPair_003_1() throws Exception {
    String[] pair = ConfData.parseToPair("  asd  : qwerty = wow  ");
    assertThat(pair).hasSize(2);
    assertThat(pair[0]).isEqualTo("asd");
    assertThat(pair[1]).isEqualTo(" qwerty = wow  ");
  }

  @Test
  public void parseToPair_004() throws Exception {
    String[] pair = ConfData.parseToPair("  asd   qwerty   ");
    assertThat(pair).hasSize(2);
    assertThat(pair[0]).isEqualTo("asd");
    assertThat(pair[1]).isEqualTo("qwerty");
  }

  @Test
  public void parseToPair_005() throws Exception {
    String[] pair = ConfData.parseToPair("  asd   ");
    assertThat(pair).hasSize(2);
    assertThat(pair[0]).isEqualTo("asd");
    assertThat(pair[1]).isNull();
  }

  @Test
  public void parseToPair_006() throws Exception {
    String[] pair = ConfData.parseToPair("  asd  { ");
    assertThat(pair).hasSize(2);
    assertThat(pair[0]).isEqualTo("asd");
    assertThat(pair[1]).isEqualTo("{");
  }

  @Test
  public void parseToPair_007() throws Exception {
    String[] pair = ConfData.parseToPair("  } ");
    assertThat(pair).hasSize(2);
    assertThat(pair[0]).isEqualTo("}");
    assertThat(pair[1]).isNull();
  }

  @Test
  public void readFromFile() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd   = dsa      ");
      out.println("hello = wow      ");
      out.println("asd   = qwerty   ");
      out.println("group = {        ");
      out.println("   sigma = asd   ");
      out.println("   wow   = 111   ");
      out.println("   pomidor {     ");
      out.println("      luka = asd ");
      out.println("      asd = 1    ");
      out.println("      dsa = 2    ");
      out.println("      luka = dsa ");
      out.println("   }             ");
      out.println("}                ");
      out.println("                 ");
      out.println("#SOS = sis       ");
      out.println("SOS = kolt1      ");
      out.println("SOS = kolt2      ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

//    System.out.println(cd.getData());

    assertThat(cd.getData().keySet()).hasSize(4);
    assertThat(cd.getData().keySet()).contains("group", "asd", "SOS", "hello");

    assertThat(cd.getData().get("SOS").get(0)).isEqualTo("kolt1");
    assertThat(cd.getData().get("SOS").get(1)).isEqualTo("kolt2");
  }

  @Test
  public void readFromFile_UTF8() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd   = Привет мир  ");
      out.println("hello = 世界，你好    ");
      out.println("dsa   = สวัสดีชาวโลก  ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    assertThat(cd.getData().get("asd").get(0)).isEqualTo("Привет мир");
    assertThat(cd.getData().get("hello").get(0)).isEqualTo("世界，你好");
    assertThat(cd.getData().get("dsa").get(0)).isEqualTo("สวัสดีชาวโลก");
  }

  @Test
  public void str_1() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd = {             ");
      out.println("  dsa = {           ");
      out.println("    status = OK     ");
      out.println("  }                 ");
      out.println("}                   ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    assertThat(cd.strEx("asd/dsa/status")).isEqualTo("OK");
  }

  @Test(expectedExceptions = NoValue.class, expectedExceptionsMessageRegExp = "asd/dsa")
  public void strEx_1() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd = {           ");
      out.println("  dsa = {         ");
      out.println("    status = OK   ");
      out.println("  }               ");
      out.println("}                 ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    cd.strEx("asd/dsa");
  }

  @Test(expectedExceptions = NoValue.class, expectedExceptionsMessageRegExp = "wow")
  public void strEx_2() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd = {              ");
      out.println("  dsa1 = {           ");
      out.println("    status2 = OK     ");
      out.println("  }                  ");
      out.println("}                    ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    cd.strEx("wow/asd");
  }

  @Test(expectedExceptions = NoValue.class)
  public void strEx_3() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd = sinus        ");
      out.println("#asd.wow = boom    ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    cd.strEx("asd.wow");
  }

  @Test
  public void str_2() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd = {              ");
      out.println("  dsa = {            ");
      out.println("    status = OK      ");
      out.println("  }                  ");
      out.println("}                    ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file.getAbsolutePath());

    assertThat(cd.str("asd/dsa", "DEF")).isEqualTo("DEF");
    assertThat(cd.str("asd/wow", "ASD")).isEqualTo("ASD");
  }

  @Test
  public void inte_1() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd = {              ");
      out.println("  dsa = {            ");
      out.println("    status = 828     ");
      out.println("  }                  ");
      out.println("}                    ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    assertThat(cd.asInt("asd/dsa/status")).isEqualTo(828);
    assertThat(cd.asInt("asd/dsa/status", 111)).isEqualTo(828);
    assertThat(cd.asInt("asd1/dsa/status", 1111)).isEqualTo(1111);
  }

  @Test
  public void asInt_2() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd = {              ");
      out.println("  dsa {              ");
      out.println("    status = 828     ");
      out.println("  }                  ");
      out.println("  wallStreet 111        ");
      out.println("  dsa {              ");
      out.println("    status = 999     ");
      out.println("  }                  ");
      out.println("}                    ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    assertThat(cd.asInt("asd/wallStreet")).isEqualTo(111);
    assertThat(cd.asInt("asd/dsa/status", 111)).isEqualTo(828);
    assertThat(cd.asInt("asd/dsa.1/status", 111)).isEqualTo(999);
    assertThat(cd.asInt("asd/dsa.2/status", 111)).isEqualTo(111);
  }

  @Test
  public void list_1() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("asd = {              ");
      out.println("  dsa {              ");
      out.println("    status = 828     ");
      out.println("    amil   = 828     ");
      out.println("    sinus  = 828     ");
      out.println("  }                  ");
      out.println("}                    ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    List<String> list = cd.list("asd/dsa");

    assertThat(list).hasSize(3);
    assertThat(list).contains("status", "amil", "sinus");
  }

  @Test
  public void list_2() throws Exception {
    Random rnd  = new Random();
    File   file = new File("build/target/confData" + rnd.nextInt(1000000000));
    file.getParentFile().mkdirs();
    {
      PrintStream out = new PrintStream(file, "UTF-8");
      out.println("    status = 828     ");
      out.println("    amil   = 828     ");
      out.println("    sinus  = 828     ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromFile(file);

    List<String> list = cd.list(null);

    assertThat(list).hasSize(3);
    assertThat(list).contains("status", "amil", "sinus");
  }

  @Test
  public void bool() throws Exception {

    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    {
      PrintStream out = new PrintStream(bOut, true, "UTF-8");
      out.println(" asd1 = 1    ");
      out.println(" asd2 = y    ");
      out.println(" asd3 = yes  ");
      out.println(" asd4 = On   ");
      out.println(" asd5 = Да   ");
      out.println(" asd6 = True ");
      out.println(" asd7 = t    ");
      out.println(" asd8 = 真相 ");
      out.close();
    }

    ConfData cd = new ConfData();
    cd.readFromByteArray(bOut.toByteArray());

    assertThat(cd.bool("asd1")).isTrue();
    assertThat(cd.bool("asd2")).isTrue();
    assertThat(cd.bool("asd3")).isTrue();
    assertThat(cd.bool("asd4")).isTrue();
    assertThat(cd.bool("asd5")).isTrue();
    assertThat(cd.bool("asd6")).isTrue();
    assertThat(cd.bool("asd7")).isTrue();
    assertThat(cd.bool("asd8")).isTrue();

    assertThat(cd.bool("wow")).isFalse();

    assertThat(cd.bool("wow", true)).isTrue();
  }

  @Test(expectedExceptions = NoValue.class, expectedExceptionsMessageRegExp = "asd")
  public void boolEx() throws Exception {
    new ConfData().boolEx("asd");
  }

  @Test(expectedExceptions = NoValue.class, expectedExceptionsMessageRegExp = "asd")
  public void dateEx3() throws Exception {
    new ConfData().dateEx("asd");
  }

  @DataProvider
  public Object[][] date_DataProvider() {
    return new Object[][]{

      new Object[]{"yyyy-MM-dd'T'HH:mm:ss.SSS", "1980-11-23T23:11:18.123"},
      new Object[]{"yyyy-MM-dd'T'HH:mm:ss", "1980-11-23T23:11:18"},
      new Object[]{"yyyy-MM-dd'T'HH:mm", "1980-11-23T23:11"},
      new Object[]{"yyyy-MM-dd", "1980-11-23"},
      new Object[]{"yyyy-MM-dd HH:mm:ss.SSS", "1980-11-23 23:11:18.123"},
      new Object[]{"yyyy-MM-dd HH:mm:ss", "1980-11-23 23:11:18"},
      new Object[]{"yyyy-MM-dd HH:mm", "1980-11-23 23:11"},

      new Object[]{"dd/MM/yyyy HH:mm:ss.SSS", "23/11/1980 23:11:18.123"},
      new Object[]{"dd/MM/yyyy HH:mm:ss", "23/11/1980 23:11:18"},
      new Object[]{"dd/MM/yyyy HH:mm", "23/11/1980 23:11"},

    };
  }

  @Test(dataProvider = "date_DataProvider")
  public void dateEx1(String format, String value) throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=" + value);

    Date expectedValue = new SimpleDateFormat(format).parse(value);

    assertThat(cd.dateEx("asd", format)).isEqualTo(expectedValue);
  }

  @Test(dataProvider = "date_DataProvider")
  public void dateEx2(String format, String value) throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=" + value);

    Date expectedValue = new SimpleDateFormat(format).parse(value);

    assertThat(cd.dateEx("asd")).isEqualTo(expectedValue);
  }

  @Test(dataProvider = "date_DataProvider")
  public void date(String format, String value) throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=" + value);

    Date expectedValue = new SimpleDateFormat(format).parse(value);

    assertThat(cd.date("asd")).isEqualTo(expectedValue);
  }

  @Test
  public void date2() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd1=1990-01-02");

    Date expectedValue = new SimpleDateFormat("dd/MM/yyyy").parse("02/01/1990");

    assertThat(cd.date("asd1")).isEqualTo(expectedValue);
  }

  @Test
  public void date3() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd2=1990-01-02");

    Date expectedValue = new SimpleDateFormat("dd/MM/yyyy").parse("02/01/1990");

    assertThat(cd.date("asd2", "yyyy-MM-dd")).isEqualTo(expectedValue);
  }

  @Test
  public void date4() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=1990-01-02");

    Date expectedValue = new SimpleDateFormat("dd/MM/yyyy").parse("02/01/1990");

    assertThat(cd.date("asd", " dd/MM/yyyy ;; yyyy-MM-dd ; ")).isEqualTo(expectedValue);
  }

  @Test
  public void date_null() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=#1990-01-02");

    assertThat(cd.date("asd", " dd/MM/yyyy ;; yyyy-MM-dd ; ")).isNull();
  }

  @Test
  public void date_defaultValue() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd1=1990-01-02");

    Date expectedValue = new SimpleDateFormat("dd/MM/yyyy").parse("12/03/1990");

    assertThat(cd.date("asd1", expectedValue, " dd/MM/yyyy ;; dd/MM/yyyy HH:mm ; ")).isEqualTo(expectedValue);
  }

  @Test(expectedExceptions = NoValue.class)
  public void asIntEx_NoValue() throws Exception {
    ConfData cd = new ConfData();

    cd.asIntEx("asd2");
  }

  @Test(expectedExceptions = NoValue.class)
  public void asLongEx_NoValue() throws Exception {
    ConfData cd = new ConfData();

    cd.asLongEx("asd");
  }

  @Test
  public void asLongEx_ok() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("dsa=345345");

    assertThat(cd.asLongEx("dsa")).isEqualTo(345345);
  }

  @Test
  public void asIntEx_ok() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=123");

    assertThat(cd.asIntEx("asd")).isEqualTo(123);
  }

  @Test
  public void asLong() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=123");

    assertThat(cd.asLong("asd")).isEqualTo(123);
    assertThat(cd.asLong("dsa")).isEqualTo(0);
  }

  @Test(expectedExceptions = java.lang.NumberFormatException.class)
  public void asLong_leftValue() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=a123");

    cd.asLong("asd");
  }

  @Test
  public void asLong_leftValue_default() throws Exception {
    ConfData cd = new ConfData();
    cd.readFromCharSequence("asd=a123");

    assertThat(cd.asLong("asd", 333)).isEqualTo(333);
    assertThat(cd.asLong("dsa", 777)).isEqualTo(777);
  }
}
