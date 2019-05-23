package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.RND;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldAcceptorCreatorTest {
  public static class SimpleFields {
    public String stringField;
    public int intField;
    public Integer intFieldBoxed;
    public long longField;
    public Long longFieldBoxed;
    public boolean booleanField;
    public Boolean booleanFieldBoxed;
  }

  @Test
  public void classSimpleFields_setter_stringField() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    String s = RND.str(10);

    map.get("stringField").setStrValue(x, s);
    assertThat(x.stringField).isEqualTo(s);
  }

  @Test
  public void classSimpleFields_setter_stringField_null() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();
    x.stringField = "asd";

    map.get("stringField").setStrValue(x, null);
    assertThat(x.stringField).isNull();
  }

  @Test
  public void classSimpleFields_setter_intField() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    int value = RND.rnd.nextInt();

    map.get("intField").setStrValue(x, "" + value);
    assertThat(x.intField).isEqualTo(value);
  }

  @Test
  public void classSimpleFields_setter_intField_null() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    map.get("intField").setStrValue(x, null);
    assertThat(x.intField).isEqualTo(0);
  }

  @Test
  public void classSimpleFields_setter_intField_empty() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    map.get("intField").setStrValue(x, "  ");
    assertThat(x.intField).isEqualTo(0);
  }

  @Test
  public void classSimpleFields_setter_intFieldBoxed() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    int value = RND.rnd.nextInt();

    map.get("intFieldBoxed").setStrValue(x, "" + value);
    assertThat(x.intFieldBoxed).isEqualTo(value);
  }

  @Test
  public void classSimpleFields_setter_intFieldBoxed_null() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();
    x.intFieldBoxed = 234;

    map.get("intFieldBoxed").setStrValue(x, null);
    assertThat(x.intFieldBoxed).isNull();
  }

  @Test
  public void classSimpleFields_setter_intFieldBoxed_empty() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();
    x.intFieldBoxed = 234;

    map.get("intFieldBoxed").setStrValue(x, "  ");
    assertThat(x.intFieldBoxed).isNull();
  }


  @Test
  public void classSimpleFields_setter_longField() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    long value = RND.rnd.nextLong();

    map.get("longField").setStrValue(x, "" + value);
    assertThat(x.longField).isEqualTo(value);
  }

  @Test
  public void classSimpleFields_setter_longField_null() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    map.get("longField").setStrValue(x, null);
    assertThat(x.longField).isEqualTo(0L);
  }

  @Test
  public void classSimpleFields_setter_longField_empty() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    map.get("longField").setStrValue(x, "  ");
    assertThat(x.longField).isEqualTo(0L);
  }

  @Test
  public void classSimpleFields_setter_longFieldBoxed() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    long value = RND.rnd.nextLong();

    map.get("longFieldBoxed").setStrValue(x, "" + value);
    assertThat(x.longFieldBoxed).isEqualTo(value);
  }

  @Test
  public void classSimpleFields_setter_longFieldBoxed_null() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();
    x.longFieldBoxed = 234L;

    map.get("longFieldBoxed").setStrValue(x, null);
    assertThat(x.longFieldBoxed).isNull();
  }

  @Test
  public void classSimpleFields_setter_longFieldBoxed_empty() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();
    x.longFieldBoxed = 234L;

    map.get("longFieldBoxed").setStrValue(x, "  ");
    assertThat(x.longFieldBoxed).isNull();
  }


  @DataProvider
  public Object[][] classSimpleFields_setter_booleanField_DataProvider() {
    return new Object[][]{
      {" true   ", true},
      {" TRUE   ", true},
      {" True   ", true},
      {" T      ", true},
      {" YES    ", true},
      {" yes    ", true},
      {" on     ", true},
      {" On     ", true},
      {" ON     ", true},
      {" Да     ", true},
      {" Истина ", true},
      {" И      ", true},
      {"   1    ", true},

      {"   0    ", false},
      {"   asd  ", false},
      {"   some left string  ", false},
      {"   Нет  ", false},
      {" False  ", false},

      {"     ", false},
      {null, null},
    };
  }

  @Test(dataProvider = "classSimpleFields_setter_booleanField_DataProvider")
  public void classSimpleFields_setter_booleanField(String settingString, Boolean expectedValue) throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.booleanField = RND.rnd.nextBoolean();

    map.get("booleanField").setStrValue(x, settingString);
    assertThat(x.booleanField).isEqualTo(expectedValue == null ? false : expectedValue);
  }

  @Test(dataProvider = "classSimpleFields_setter_booleanField_DataProvider")
  public void classSimpleFields_setter_booleanFieldBoxed(String settingString, Boolean expectedValue) throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.booleanFieldBoxed = RND.rnd.nextBoolean() ? RND.rnd.nextBoolean() : null;

    map.get("booleanFieldBoxed").setStrValue(x, settingString);
    assertThat(x.booleanFieldBoxed).isEqualTo(expectedValue);
  }


  @Test
  public void classSimpleFields_getter_stringField() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    String s = RND.str(10);
    x.stringField = s;

    assertThat(map.get("stringField").getStrValue(x)).isEqualTo(s);
  }

  @Test
  public void classSimpleFields_getter_stringField_null() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.stringField = null;

    assertThat(map.get("stringField").getStrValue(x)).isNull();
  }

  @Test
  public void classSimpleFields_getter_stringField_empty() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.stringField = "";

    assertThat(map.get("stringField").getStrValue(x)).isEmpty();
  }

  @Test
  public void classSimpleFields_getter_intField() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.intField = RND.rnd.nextInt();

    assertThat(map.get("intField").getStrValue(x)).isEqualTo("" + x.intField);
  }

  @Test
  public void classSimpleFields_getter_intField_zero() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.intField = 0;

    assertThat(map.get("intField").getStrValue(x)).isEqualTo("0");
  }

  @Test
  public void classSimpleFields_getter_intFieldBoxed() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.intFieldBoxed = RND.rnd.nextInt();

    assertThat(map.get("intFieldBoxed").getStrValue(x)).isEqualTo("" + x.intFieldBoxed);
  }

  @Test
  public void classSimpleFields_getter_intFieldBoxed_null() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.intFieldBoxed = null;

    assertThat(map.get("intFieldBoxed").getStrValue(x)).isNull();
  }

  @Test
  public void classSimpleFields_getter_intFieldBoxed_zero() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.intFieldBoxed = 0;

    assertThat(map.get("intFieldBoxed").getStrValue(x)).isEqualTo("0");
  }


  @Test
  public void classSimpleFields_getter_longField() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.longField = RND.rnd.nextLong();

    assertThat(map.get("longField").getStrValue(x)).isEqualTo("" + x.longField);
  }

  @Test
  public void classSimpleFields_getter_longField_zero() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.longField = 0;

    assertThat(map.get("longField").getStrValue(x)).isEqualTo("0");
  }

  @Test
  public void classSimpleFields_getter_longFieldBoxed() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.longFieldBoxed = RND.rnd.nextLong();

    assertThat(map.get("longFieldBoxed").getStrValue(x)).isEqualTo("" + x.longFieldBoxed);
  }

  @Test
  public void classSimpleFields_getter_longFieldBoxed_null() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.longFieldBoxed = null;

    assertThat(map.get("longFieldBoxed").getStrValue(x)).isNull();
  }

  @Test
  public void classSimpleFields_getter_longFieldBoxed_zero() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(SimpleFields.class);
    SimpleFields x = new SimpleFields();

    x.longFieldBoxed = 0L;

    assertThat(map.get("longFieldBoxed").getStrValue(x)).isEqualTo("0");
  }


  public static class NoGetterForField {
    @SuppressWarnings("unused")
    private int field;

    @SuppressWarnings("unused")
    public void setField(int field) {
      this.field = field;
    }
  }

  @Test
  public void testNoGetterForField() throws Exception {
    assertThat(FieldAcceptorCreator.createList(NoGetterForField.class)).isEmpty();
  }

  public static class NoSetterForField {
    @SuppressWarnings("unused")
    private int field;

    @SuppressWarnings("unused")
    public int getField() {
      return field;
    }
  }

  @Test
  public void testNoSetterForField() throws Exception {
    assertThat(FieldAcceptorCreator.createList(NoSetterForField.class)).isEmpty();
  }

  public static class WithGetterForField {
    public int field;

    @SuppressWarnings("unused")
    public int getField() {
      return field + 3;
    }
  }

  @Test
  public void testWithGetterForField() throws Exception {

    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(WithGetterForField.class);

    WithGetterForField x = new WithGetterForField();

    map.get("field").setStrValue(x, "234");

    assertThat(x.field).isEqualTo(234);

    assertThat(map.get("field").getStrValue(x)).isEqualTo("237");
  }


  public static class WithSetterForField {
    public int field;

    @SuppressWarnings("unused")
    public void setField(int field) {
      this.field = field + 3;
    }
  }

  @Test
  public void testWithSetterForField() throws Exception {

    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(WithSetterForField.class);

    WithSetterForField x = new WithSetterForField();

    x.field = 5435;

    map.get("field").setStrValue(x, "100");

    assertThat(x.field).isEqualTo(103);

    x.field = 1010;

    assertThat(map.get("field").getStrValue(x)).isEqualTo("1010");
  }

  public static class AllAcceptors {
    public int field;

    @SuppressWarnings("unused")
    public void setField(int field) {
      this.field = field + 3;
    }

    @SuppressWarnings("unused")
    public int getField() {
      return field + 7;
    }
  }

  @Test
  public void testAllAcceptors() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(AllAcceptors.class);

    AllAcceptors x = new AllAcceptors();

    x.field = 1000;

    assertThat(map.get("field").getStrValue(x)).isEqualTo("1007");

    map.get("field").setStrValue(x, "700");

    assertThat(x.field).isEqualTo(703);
  }


  public static class AllAcceptorsBoolean {
    public boolean field;

    public boolean setterCalled = false;
    public boolean getterCalled = false;

    @SuppressWarnings("unused")
    public boolean isField() {
      getterCalled = true;
      return field;
    }

    @SuppressWarnings("unused")
    public void setField(boolean field) {
      this.field = field;
      setterCalled = true;
    }
  }

  @Test
  public void testAllAcceptors_boolean() throws Exception {
    Map<String, FieldAcceptor> map = FieldAcceptorCreator.createMap(AllAcceptorsBoolean.class);

    AllAcceptorsBoolean x = new AllAcceptorsBoolean();

    x.field = true;
    x.getterCalled = false;

    assertThat(map.get("field").getStrValue(x)).isEqualTo("true");
    assertThat(x.getterCalled).isTrue();

    x.field = false;
    x.getterCalled = false;

    assertThat(map.get("field").getStrValue(x)).isEqualTo("false");
    assertThat(x.getterCalled).isTrue();

    x.setterCalled = false;
    x.field = false;
    map.get("field").setStrValue(x, "ON");

    assertThat(x.setterCalled).isTrue();
    assertThat(x.field).isTrue();

    x.setterCalled = false;
    x.field = true;
    map.get("field").setStrValue(x, "Off");

    assertThat(x.setterCalled).isTrue();
    assertThat(x.field).isFalse();

  }
}
