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

  @Description(ForCheckDefaultValue_DESCRIPTION)
  interface ForCheckDefaultValue {
    @SuppressWarnings("unused")
    String f00();

    @SuppressWarnings("unused")
    @DefaultStrValue(DEF1)
    String f01();

    @SuppressWarnings("unused")
    @DefaultIntValue(DEF2)
    String f02();

    @SuppressWarnings("unused")
    @DefaultLongValue(DEF3)
    String f03();

    @SuppressWarnings("unused")
    @DefaultBoolValue(DEF4)
    String f04();

    @SuppressWarnings("unused")
    @DefaultBoolValue(DEF5)
    String f05();


    @SuppressWarnings("unused")
    int f06();

    @SuppressWarnings("unused")
    @DefaultIntValue(DEF7)
    int f07();

    @SuppressWarnings("unused")
    @DefaultLongValue(DEF8)
    int f08();

    @SuppressWarnings("unused")
    @DefaultStrValue(DEF9)
    int f09();

    @SuppressWarnings("unused")
    @DefaultBoolValue(DEF10)
    int f10();

    @SuppressWarnings("unused")
    @DefaultBoolValue(DEF11)
    int f11();

    @SuppressWarnings("unused")
    Integer f12();


    @SuppressWarnings("unused")
    long f13();

    @SuppressWarnings("unused")
    @DefaultIntValue(DEF14)
    long f14();

    @SuppressWarnings("unused")
    @DefaultLongValue(DEF15)
    long f15();

    @SuppressWarnings("unused")
    @DefaultStrValue(DEF16)
    long f16();

    @SuppressWarnings("unused")
    @DefaultBoolValue(DEF17)
    long f17();

    @SuppressWarnings("unused")
    @DefaultBoolValue(DEF18)
    long f18();

    @SuppressWarnings("unused")
    Long f19();

    @SuppressWarnings("unused")
    boolean f20();

    @SuppressWarnings("unused")
    Boolean f21();

    @SuppressWarnings("unused")
    @DefaultBoolValue(DEF22)
    boolean f22();

    @SuppressWarnings("unused")
    @DefaultBoolValue(DEF23)
    boolean f23();

    @SuppressWarnings("unused")
    @DefaultStrValue("" + DEF24)
    boolean f24();

    @SuppressWarnings("unused")
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
}