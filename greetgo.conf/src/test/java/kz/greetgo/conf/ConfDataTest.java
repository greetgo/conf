package kz.greetgo.conf;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Random;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
public class ConfDataTest {
  @Test
  public void parseToPair_001() throws Exception {
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
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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

    System.out.println(cd.getData());

    assertThat(cd.getData().keySet()).hasSize(4);
    assertThat(cd.getData().keySet()).contains("group", "asd", "SOS", "hello");

    assertThat(cd.getData().get("SOS").get(0)).isEqualTo("kolt1");
    assertThat(cd.getData().get("SOS").get(1)).isEqualTo("kolt2");
  }

  @Test
  public void readFromFile_UTF8() throws Exception {
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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

  @Test
  public void str_2() throws Exception {
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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
    cd.readFromFile(file);

    assertThat(cd.str("asd/dsa", "DEF")).isEqualTo("DEF");
  }

  @Test
  public void inte_1() throws Exception {
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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
    assertThat(cd.asInt("asd1/dsa/status", 111)).isEqualTo(111);
  }

  @Test
  public void asInt_2() throws Exception {
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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
    Random rnd = new Random();
    File file = new File("target/confData" + rnd.nextInt(1000000000));
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
}
