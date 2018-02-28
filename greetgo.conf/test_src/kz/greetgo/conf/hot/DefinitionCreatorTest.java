package kz.greetgo.conf.hot;

import kz.greetgo.conf.ConfUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static kz.greetgo.conf.hot.DefinitionCreator.createDefinition;
import static org.fest.assertions.api.Assertions.assertThat;

public class DefinitionCreatorTest {

  private static final String DEF0 = null;
  private static final String DEF1 = "h4b32jh5b436hj2b6v";
  private static final int DEF2 = 54321534;
  private static final long DEF3 = 871894174900322131L;
  private static final boolean DEF4 = false;
  private static final boolean DEF5 = true;

  private static final int DEF6 = 0;
  private static final int DEF7 = 543004277;
  private static final long DEF8 = 4321453;
  private static final String DEF9 = "432154";
  private static final boolean DEF10 = false;
  private static final boolean DEF11 = true;
  private static final Integer DEF12 = null;

  private static final long DEF13 = 0;
  private static final int DEF14 = 8176465;
  private static final long DEF15 = 4376158561434L;
  private static final String DEF16 = "5432665386489";
  private static final boolean DEF17 = false;
  private static final boolean DEF18 = true;
  private static final Long DEF19 = null;

  private static final boolean DEF20 = false;
  private static final Boolean DEF21 = null;
  private static final boolean DEF22 = false;
  private static final boolean DEF23 = true;
  private static final boolean DEF24 = false;
  private static final boolean DEF25 = true;

  private static final String ForCheckDefaultValue_DESCRIPTION = "ForCheckDefaultValue DESCRIPTION";

  @SuppressWarnings("unused")
  @Description(ForCheckDefaultValue_DESCRIPTION)
  interface ForCheckDefaultValue {

    String f00();

    @DefaultStrValue(DEF1)
    String f01();

    @DefaultIntValue(DEF2)
    String f02();

    @DefaultLongValue(DEF3)
    String f03();

    @DefaultBoolValue(DEF4)
    String f04();

    @DefaultBoolValue(DEF5)
    String f05();


    int f06();

    @DefaultIntValue(DEF7)
    int f07();

    @DefaultLongValue(DEF8)
    int f08();

    @DefaultStrValue(DEF9)
    int f09();

    @DefaultBoolValue(DEF10)
    int f10();

    @DefaultBoolValue(DEF11)
    int f11();

    Integer f12();


    long f13();

    @DefaultIntValue(DEF14)
    long f14();

    @DefaultLongValue(DEF15)
    long f15();

    @DefaultStrValue(DEF16)
    long f16();

    @DefaultBoolValue(DEF17)
    long f17();

    @DefaultBoolValue(DEF18)
    long f18();

    Long f19();

    boolean f20();

    Boolean f21();

    @DefaultBoolValue(DEF22)
    boolean f22();

    @DefaultBoolValue(DEF23)
    boolean f23();

    @DefaultStrValue("" + DEF24)
    boolean f24();

    @DefaultStrValue("" + DEF25)
    boolean f25();
  }

  @DataProvider
  public Object[][] checkDefaultValue_DP() {
    //noinspection ConstantConditions
    return new Object[][]{

      {0, DEF0},
      {1, DEF1},
      {2, "" + DEF2},
      {3, "" + DEF3},
      {4, "" + DEF4},
      {5, "" + DEF5},

      {6, DEF6},
      {7, DEF7},
      {8, (int) DEF8},
      {9, Integer.parseInt(DEF9)},
      {10, DEF10 ? 1 : 0},
      {11, DEF11 ? 1 : 0},
      {12, DEF12},

      {13, DEF13},
      {14, (long) DEF14},
      {15, DEF15},
      {16, Long.parseLong(DEF16)},
      {17, DEF17 ? 1L : 0L},
      {18, DEF18 ? 1L : 0L},
      {19, DEF19},

      {20, DEF20},
      {21, DEF21},
      {22, DEF22},
      {23, DEF23},
      {24, DEF24},
      {25, DEF25},
    };
  }

  @Test(dataProvider = "checkDefaultValue_DP")
  public void createDefinition_checkDefaultValue(int index, Object defaultValue) throws Exception {

    String location = ConfUtil.rndStr(10);

    //
    //
    HotConfigDefinition definition = createDefinition(location, ForCheckDefaultValue.class, Function.identity());
    //
    //

    assertThat(definition).isNotNull();
    assertThat(definition.location()).isEqualTo(location);
    assertThat(definition.description()).isEqualTo(ForCheckDefaultValue_DESCRIPTION);

    List<ElementDefinition> elementDefinitions = new ArrayList<>(definition.elementList());
    elementDefinitions.sort(Comparator.comparing(a -> a.name));

    ElementDefinition elem = elementDefinitions.get(index);
    assertThat(elem.defaultValue)
      .describedAs("index = " + index + ", method name = " + elem.name)
      .isEqualTo(defaultValue);
  }

  interface ForReplacer {
    @SuppressWarnings("unused")
    @DefaultStrValue("Default value WOW")
    String asd();
  }

  @Test
  public void createDefinition_replacer() throws Exception {
    Function<String, String> replacer = s -> s.replaceAll("WOW", "SIN");

    //
    //
    HotConfigDefinition definition = createDefinition("", ForReplacer.class, replacer);
    //
    //

    assertThat(definition.elementList().get(0).defaultValue).isEqualTo("Default value SIN");
  }

  private static final String ForDescription_1 = "ForDescription 1";
  private static final String ForDescription_2 = "ForDescription 2";

  @Description(ForDescription_1)
  interface ForDescription {
    @SuppressWarnings("unused")
    @Description(ForDescription_2)
    String fieldNameHelloWorld();
  }

  @Test
  public void createDefinition_description_name() throws Exception {

    //
    //
    HotConfigDefinition definition = createDefinition("", ForDescription.class, Function.identity());
    //
    //

    assertThat(definition.description()).isEqualTo(ForDescription_1);
    assertThat(definition.elementList().get(0).description).isEqualTo(ForDescription_2);
    assertThat(definition.elementList().get(0).name).isEqualTo("fieldNameHelloWorld");
  }

  abstract class LeftType {}

  interface ForLeftType {
    @SuppressWarnings("unused")
    LeftType asd();
  }

  @Test(expectedExceptions = CannotGenerateDefaultValue.class)
  public void createDefinition_ForLeftType() throws Exception {
    //
    //
    createDefinition("", ForLeftType.class, Function.identity());
    //
    //
  }

  interface ForLocation {
    @SuppressWarnings("unused")
    int a();
  }

  @Test
  public void createDefinition_location() throws Exception {

    String location = ConfUtil.rndStr(10);

    //
    //
    HotConfigDefinition definition = createDefinition(location, ForLocation.class, Function.identity());
    //
    //

    assertThat(definition.location()).isEqualTo(location);
  }

  @SuppressWarnings("unused")
  public static class ElemClass {

    @DefaultLongValue(2344324L)
    long longField;

    @DefaultIntValue(234)
    int intField;

    @DefaultStrValue("HI dsa2343ddf4")
    String strField;

    @DefaultBoolValue(true)
    boolean boolField;

  }

  interface ElemClassConfig {

    @SuppressWarnings("unused")
    ElemClass field();

  }

  @Test
  public void createDefinition_ElemClass_defaultValue() throws Exception {

    //
    //
    HotConfigDefinition definition = createDefinition("", ElemClassConfig.class, Function.identity());
    //
    //

    assertThat(definition.elementList().get(0).defaultValue).isInstanceOf(ElemClass.class);

    ElemClass defaultValue = (ElemClass) definition.elementList().get(0).defaultValue;

    assertThat(defaultValue.longField).isEqualTo(2344324L);
    assertThat(defaultValue.intField).isEqualTo(234);
    assertThat(defaultValue.strField).isEqualTo("HI dsa2343ddf4");
    assertThat(defaultValue.boolField).isTrue();
  }
}
