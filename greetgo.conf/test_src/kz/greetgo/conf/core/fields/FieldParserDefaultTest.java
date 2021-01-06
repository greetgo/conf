package kz.greetgo.conf.core.fields;

import kz.greetgo.conf.RND;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldParserDefaultTest {

  public static class Model1 {

    public String field1;

    public int field2;

    @ConfIgnore
    public String leftField;

  }

  @Test
  public void parse__01() {

    FieldParserDefault parser = new FieldParserDefault();

    //
    //
    Map<String, FieldAccess> map = parser.parse(Model1.class);
    //
    //

    Model1 model = new Model1();
    model.field1 = RND.str(10);

    assertThat(map.get("field1").getValue(model)).isEqualTo(model.field1);

    map.get("field2").setValue(model, 1092347347);
    assertThat(model.field2).isEqualTo(1092347347);

    assertThat(map.get("field1").type()).isEqualTo(String.class);

    model.leftField = RND.str(2);
    assertThat(map).doesNotContainKey("leftField");
  }

  @SuppressWarnings("unused")
  public static class Model2 {

    public  String field1;
    private String field2;

    public String getField1() {
      return field1 + " GET1";
    }

    public void setField1(String field1) {
      this.field1 = field1 + " SET1";
    }

    public String getField2() {
      return field2 + " GET2";
    }

    public void setField2(String field2) {
      this.field2 = field2 + " SET2";
    }

    @ConfIgnore
    public String leftField1;

    public String getLeftField1() {
      return leftField1;
    }

    public void setLeftField1(String leftField1) {
      this.leftField1 = leftField1;
    }

    public String leftField2;

    @ConfIgnore
    public String getLeftField2() {
      return leftField2;
    }

    public void setLeftField2(String leftField2) {
      this.leftField2 = leftField2;
    }

    public String leftField3;

    public String getLeftField3() {
      return leftField3;
    }

    @ConfIgnore
    public void setLeftField3(String leftField3) {
      this.leftField3 = leftField3;
    }
  }

  @Test
  public void parse__02() {

    FieldParserDefault parser = new FieldParserDefault();

    //
    //
    Map<String, FieldAccess> map = parser.parse(Model2.class);
    //
    //

    Model2 model = new Model2();

    model.field1 = RND.str(10);
    assertThat(map.get("field1").getValue(model)).isEqualTo(model.field1 + " GET1");

    map.get("field1").setValue(model, "leak");
    assertThat(model.field1).isEqualTo("leak SET1");

    model.field2 = "cool";
    assertThat(map.get("field2").getValue(model)).isEqualTo("cool GET2");

    map.get("field2").setValue(model, "stone");
    assertThat(model.field2).isEqualTo("stone SET2");

    assertThat(map.get("field1").type()).isEqualTo(String.class);
  }

  @Test
  public void parse__02_leftFields() {

    FieldParserDefault parser = new FieldParserDefault();

    //
    //
    Map<String, FieldAccess> map = parser.parse(Model2.class);
    //
    //

    assertThat(map).doesNotContainKey("leftField1");
    assertThat(map).doesNotContainKey("leftField2");
    assertThat(map).doesNotContainKey("leftField3");
  }

}
