package kz.greetgo.conf;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static kz.greetgo.conf.ConfUtil.convertToType;
import static org.fest.assertions.api.Assertions.assertThat;

public class ConfUtilTest {
  @Test
  public void convertToType_null() throws Exception {
    assertThat(convertToType(null, null)).isNull();
  }

  @Test
  public void convertToType_int1() throws Exception {
    assertThat(convertToType("11", Integer.class)).isEqualTo(11);
  }

  @Test
  public void convertToType_int2() throws Exception {
    assertThat(convertToType("11", Integer.TYPE)).isEqualTo(11);
  }

  @Test
  public void convertToType_long1() throws Exception {
    assertThat(convertToType("11", Long.class)).isEqualTo(11L);
  }

  @Test
  public void convertToType_long2() throws Exception {
    assertThat(convertToType("11", Long.TYPE)).isEqualTo(11L);
  }

  @Test
  public void convertToType_double1() throws Exception {
    assertThat(convertToType("11.5", Double.class)).isEqualTo(11.5);
  }

  @Test
  public void convertToType_double2() throws Exception {
    assertThat(convertToType("11.2", Double.TYPE)).isEqualTo(11.2);
  }

  @Test
  public void convertToType_float1() throws Exception {
    assertThat(convertToType("11.5", Float.class)).isEqualTo(11.5f);
  }

  @Test
  public void convertToType_float2() throws Exception {
    assertThat(convertToType("11.2", Float.TYPE)).isEqualTo(11.2f);
  }

  @Test
  public void convertToType_str() throws Exception {
    assertThat(convertToType("hello", String.class)).isEqualTo("hello");
  }

  @Test
  public void convertToType_BD() throws Exception {
    assertThat(convertToType("11.567", BigDecimal.class)).isEqualTo(new BigDecimal("11.567"));
  }

  @Test
  public void convertToType_bool1_true() throws Exception {
    assertThat(convertToType("true", Boolean.class)).isEqualTo(true);
    assertThat(convertToType("TRUE", Boolean.class)).isEqualTo(true);
    assertThat(convertToType("on", Boolean.class)).isEqualTo(true);
    assertThat(convertToType("ON", Boolean.class)).isEqualTo(true);
    assertThat(convertToType("1", Boolean.class)).isEqualTo(true);
    assertThat(convertToType("y", Boolean.class)).isEqualTo(true);
    assertThat(convertToType("Y", Boolean.class)).isEqualTo(true);
    assertThat(convertToType("yes", Boolean.class)).isEqualTo(true);
    assertThat(convertToType("YES", Boolean.class)).isEqualTo(true);
  }

  @Test
  public void convertToType_bool1_false() throws Exception {
    assertThat(convertToType("false", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("FALSE", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("off", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("OFF", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("0", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("n", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("N", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("no", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("NO", Boolean.class)).isEqualTo(false);

    assertThat(convertToType("", Boolean.class)).isEqualTo(false);
    assertThat(convertToType("gfghfgfd", Boolean.class)).isEqualTo(false);
  }

  public void convertToType_bool2_true() throws Exception {
    assertThat(convertToType("true", Boolean.TYPE)).isEqualTo(true);
    assertThat(convertToType("TRUE", Boolean.TYPE)).isEqualTo(true);
    assertThat(convertToType("on", Boolean.TYPE)).isEqualTo(true);
    assertThat(convertToType("ON", Boolean.TYPE)).isEqualTo(true);
    assertThat(convertToType("1", Boolean.TYPE)).isEqualTo(true);
    assertThat(convertToType("y", Boolean.TYPE)).isEqualTo(true);
    assertThat(convertToType("Y", Boolean.TYPE)).isEqualTo(true);
    assertThat(convertToType("yes", Boolean.TYPE)).isEqualTo(true);
    assertThat(convertToType("YES", Boolean.TYPE)).isEqualTo(true);
  }

  @Test
  public void convertToType_bool2_false() throws Exception {
    assertThat(convertToType("false", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("FALSE", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("off", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("OFF", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("0", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("n", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("N", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("no", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("NO", Boolean.TYPE)).isEqualTo(false);

    assertThat(convertToType("", Boolean.TYPE)).isEqualTo(false);
    assertThat(convertToType("gfghfgfd", Boolean.TYPE)).isEqualTo(false);
  }

  @DataProvider
  Iterator<Object[]> convertToType_DP() {
    List<Object[]> ret = new ArrayList<>();

    ret.add(new Object[]{"yyyy-MM-dd", "2012-01-02"});
    ret.add(new Object[]{"yyyy-MM-dd HH:mm", "2012-01-02 23:11"});
    ret.add(new Object[]{"yyyy-MM-dd HH:mm:ss", "2012-01-02 23:11:34"});
    ret.add(new Object[]{"yyyy-MM-dd HH:mm:ss.SSS", "2012-01-02 23:11:34.345"});

    ret.add(new Object[]{"yyyy-MM-dd/HH:mm:ss.SSS", "2012-01-02/23:11:34.345"});

    ret.add(new Object[]{"HH:mm:ss.SSS", "23:11:34.345"});
    ret.add(new Object[]{"HH:mm:ss", "23:11:34"});
    ret.add(new Object[]{"HH:mm", "23:11"});

    ret.add(new Object[]{"dd/MM/yyyy HH:mm:ss.SSS", "02/01/2012 23:11:34.345"});
    ret.add(new Object[]{"dd/MM/yyyy HH:mm:ss", "02/01/2012 23:11:34"});
    ret.add(new Object[]{"dd/MM/yyyy HH:mm", "02/01/2012 23:11"});
    ret.add(new Object[]{"dd/MM/yyyy", "02/01/2012"});

    ret.add(new Object[]{"dd.MM.yyyy HH:mm:ss.SSS", "02.01.2012 23:11:34.345"});
    ret.add(new Object[]{"dd.MM.yyyy HH:mm:ss", "02.01.2012 23:11:34"});
    ret.add(new Object[]{"dd.MM.yyyy HH:mm", "02.01.2012 23:11"});
    ret.add(new Object[]{"dd.MM.yyyy", "02.01.2012"});

    return ret.iterator();
  }

  @Test(dataProvider = "convertToType_DP")
  public void convertToType_date(String pattern, String str) throws Exception {
    SimpleDateFormat F = new SimpleDateFormat(pattern);
    assertThat(convertToType(str, Date.class)).isEqualTo(F.parse(str));
  }

  public static final class Asd {
    public int intField1;
    public Integer intField2;
    private int intField3;

    public int getIntField3() {
      return intField3;
    }

    public void setIntField3(int intField3) {
      this.intField3 = intField3;
    }
  }

  @Test
  public void readFromStream() throws Exception {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    {
      PrintStream out = new PrintStream(bout, true, "UTF-8");
      out.println("intField1 678");
      out.println("intField2 876");
      out.println("intField3 111");
      out.close();
    }
    ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
    Asd asd = new Asd();
    ConfUtil.readFromStream(asd, in);

    assertThat(asd.intField1).isEqualTo(678);
    assertThat(asd.intField2).isEqualTo(876);
    assertThat(asd.getIntField3()).isEqualTo(111);
  }
}
