package kz.greetgo.conf.hot;

import kz.greetgo.conf.ConfUtil;
import org.assertj.core.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static kz.greetgo.conf.hot.DefinitionCreator.createDefinition;
import static org.assertj.core.api.Assertions.assertThat;

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
  public void createDefinition_checkDefaultValue(int index, Object defaultValue) {

    String location = ConfUtil.rndStr(10);

    //
    //
    HotConfigDefinition definition = createDefinition(location, ForCheckDefaultValue.class, Function.identity(), false);
    //
    //

    assertThat(definition).isNotNull();
    assertThat(definition.location()).isEqualTo(location);
    assertThat(definition.description()).isEqualTo(ForCheckDefaultValue_DESCRIPTION);

    List<ElementDefinition> elementDefinitions = new ArrayList<>(definition.elementList());
    elementDefinitions.sort(Comparator.comparing(a -> a.name));

    ElementDefinition elem = elementDefinitions.get(index);

    assertThat(elem.defaultListSize).isNull();

    assertThat(elem.newDefaultValue())
      .describedAs("index = " + index + ", method name = " + elem.name)
      .isEqualTo(defaultValue);
  }

  interface ForReplacer {
    @SuppressWarnings("unused")
    @DefaultStrValue("Default value WOW")
    String asd();
  }

  @Test
  public void createDefinition_replacer() {
    Function<String, String> replacer = s -> s.replaceAll("WOW", "SIN");

    //
    //
    HotConfigDefinition definition = createDefinition("", ForReplacer.class, replacer, false);
    //
    //

    assertThat(definition.elementList().get(0).newDefaultValue()).isEqualTo("Default value SIN");
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
  public void createDefinition_description_name() {

    //
    //
    HotConfigDefinition definition = createDefinition("", ForDescription.class, Function.identity(), false);
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

  @Test
  public void createDefinition_ForLeftType() {
    try {
      //
      //
      createDefinition("", ForLeftType.class, Function.identity(), false);
      //
      //
      Assertions.fail("Must be error");
    } catch (CannotWorkWithType e) {

      assertThat(e.workingType).isNotNull();
      assertThat(e.workingType.getName()).isEqualTo(LeftType.class.getName());
      assertThat(e.method).isNotNull();
      assertThat(e.method.getName()).isEqualTo("asd");
      assertThat(e.configInterface).isNotNull();
      assertThat(e.configInterface.getName()).isEqualTo(ForLeftType.class.getName());
    }
  }

  interface ForLocation {
    @SuppressWarnings("unused")
    int a();
  }

  @Test
  public void createDefinition_location() {

    String location = ConfUtil.rndStr(10);

    //
    //
    HotConfigDefinition definition = createDefinition(location, ForLocation.class, Function.identity(), false);
    //
    //

    assertThat(definition.location()).isEqualTo(location);
  }

  @SuppressWarnings("unused")
  public static class ElemClass {
    public long longField = 2344324;
    public int intField = 234;
    public String strField = "HI dsa2343ddf4";
    public boolean boolField = true;
  }

  interface ElemClassConfig {

    @SuppressWarnings("unused")
    ElemClass field();

  }

  @Test
  public void createDefinition_ElemClass_defaultValue() {

    //
    //
    HotConfigDefinition definition = createDefinition("", ElemClassConfig.class, Function.identity(), false);
    //
    //

    assertThat(definition.elementList().get(0).newDefaultValue()).isInstanceOf(ElemClass.class);

    assertThat(definition.elementList().get(0).defaultValue).isNull();

    ElemClass defaultValue = (ElemClass) definition.elementList().get(0).newDefaultValue();

    assertThat(defaultValue.longField).isEqualTo(2344324L);
    assertThat(defaultValue.intField).isEqualTo(234);
    assertThat(defaultValue.strField).isEqualTo("HI dsa2343ddf4");
    assertThat(defaultValue.boolField).isTrue();
  }

  interface ForTooManyDefaultAnnotations {

    @SuppressWarnings("unused")
    @DefaultStrValue("csa23")
    @DefaultIntValue(123)
    @DefaultLongValue(234L)
    int fieldA367283();
  }

  @Test
  public void createDefinition_TooManyDefaultAnnotations() {

    try {
      //
      //
      createDefinition("", ForTooManyDefaultAnnotations.class, Function.identity(), false);
      //
      //

      Assertions.fail("Must was error");
    } catch (TooManyDefaultAnnotations e) {
      assertThat(e.annotations).isEqualTo(Arrays.asList(
        "DefaultStrValue(csa23)", "DefaultIntValue(123)", "DefaultLongValue(234)"
      ));
      assertThat(e.configInterface).isNotNull();
      assertThat(e.method).isNotNull();

      assertThat(e.configInterface.getName()).isEqualTo(ForTooManyDefaultAnnotations.class.getName());
      assertThat(e.method.getName()).isEqualTo("fieldA367283");
    }
  }

  public static class ListElementType {}

  interface ListElemClassConfig {

    @SuppressWarnings("unused")
    @Description("description 4325434324h3546")
    List<ListElementType> fieldList();

  }

  @Test
  public void createDefinition_ElemClass_list() {

    //
    //
    HotConfigDefinition definition = createDefinition("", ListElemClassConfig.class, Function.identity(), false);
    //
    //

    assertThat(definition).isNotNull();
    assertThat(definition.elementList()).hasSize(1);

    ElementDefinition def = definition.elementList().get(0);

    assertThat(def.defaultListSize).isEqualTo(1);
    assertThat(def.newDefaultValue()).isInstanceOf(ListElementType.class);
    assertThat(def.name).isEqualTo("fieldList");
    assertThat(def.description).isEqualTo("description 4325434324h3546");
  }

  interface ListElemPrimitiveConfig {

    @SuppressWarnings("unused")
    @Description("description 43hb5435hb25")
    List<Integer> fieldList();

  }

  @Test
  public void createDefinition_int_list() {

    //
    //
    HotConfigDefinition definition = createDefinition("", ListElemPrimitiveConfig.class, Function.identity(), false);
    //
    //

    assertThat(definition).isNotNull();
    assertThat(definition.elementList()).hasSize(1);

    ElementDefinition def = definition.elementList().get(0);

    assertThat(def.defaultListSize).isEqualTo(1);
    assertThat(def.newDefaultValue()).isNull();
    assertThat(def.name).isEqualTo("fieldList");
    assertThat(def.description).isEqualTo("description 43hb5435hb25");
  }

  public static class SomeClass {}

  interface ForExtractClass {
    List<SomeClass> someList();

    SomeClass someField();
  }

  @Test
  public void extractClass_fromList() throws NoSuchMethodException {

    Type genericReturnType = ForExtractClass.class.getMethod("someList").getGenericReturnType();

    Class<?> aClass = DefinitionCreator.extractClass(genericReturnType);

    assertThat(aClass.getName()).isEqualTo(SomeClass.class.getName());
  }

  @Test
  public void extractClass_direct() throws NoSuchMethodException {

    Type genericReturnType = ForExtractClass.class.getMethod("someField").getGenericReturnType();

    Class<?> aClass = DefinitionCreator.extractClass(genericReturnType);

    assertThat(aClass.getName()).isEqualTo(SomeClass.class.getName());
  }

  interface HasDefListSize {

    @DefaultListSize(10)
    @SuppressWarnings("unused")
    List<String> strField();

  }

  @Test
  public void extractClass_DefaultListSize() {
    //
    //
    HotConfigDefinition definition = createDefinition("", HasDefListSize.class, Function.identity(), false);
    //
    //

    ElementDefinition elementDefinition = definition.elementList().get(0);
    assertThat(elementDefinition.defaultListSize).isEqualTo(10);
  }

  interface HasDefListSizeForSomeClass {

    @DefaultListSize(13)
    @SuppressWarnings("unused")
    List<SomeClass> someList();

  }

  @Test
  public void extractClass_DefaultListSize_for_SomeClass() {
    //
    //
    HotConfigDefinition definition = createDefinition("", HasDefListSizeForSomeClass.class, Function.identity(), false);
    //
    //

    ElementDefinition elementDefinition = definition.elementList().get(0);
    assertThat(elementDefinition.defaultListSize).isEqualTo(13);
  }
}
