package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.ConfUtil;
import kz.greetgo.conf.hot.ConfigLine;
import kz.greetgo.conf.hot.ConvertingError;
import kz.greetgo.conf.hot.HotConfigConstants;
import kz.greetgo.conf.hot.LineStructure;
import kz.greetgo.conf.hot.ReadElement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TypeManagerPrimitive implements TypeManager {

  public final Class<?> type;

  public TypeManagerPrimitive(Class<?> type) {
    if (!forMe(type)) throw new IllegalArgumentException("Wrong type " + type);
    this.type = type;
  }

  @Override
  public Object newDefaultValue(Object defaultValue) {
    return defaultValue;
  }

  public static boolean forMe(Class<?> type) {
    return type.isPrimitive() || ConfUtil.isWrapper(type) || type == String.class;
  }

  @Override
  public Object extractDefaultValue(Annotation[] annotations, Function<String, String> parameterReplacer) {
    String strValue = ConfUtil.extractStrDefaultValue(annotations, parameterReplacer);
    return ConfUtil.convertToType(strValue, type);
  }

  @Override
  public LineStructure createLineStructure(String topFieldName, Object defaultValue,
                                           String description, Integer defaultListSize) {

    boolean isList = defaultListSize != null;

    class Data {
      List<Object> list = new ArrayList<>();

      void setValue(int index, Object value) {
        assertExists(index);
        list.set(index, value);
      }

      void assertExists(int index) {
        while (list.size() <= index) {
          list.add(defaultValue);
        }
      }

      List listValue() {
        return new ArrayList<>(list);
      }

      Object firstValue() {
        return list.get(0);
      }

      final Set<String> storedSet = new HashSet<>();

      void markStored(String fullName) {
        storedSet.add(fullName);
      }

      boolean isStored(String fullName) {
        return storedSet.contains(fullName);
      }
    }

    final Data d = new Data();
    d.assertExists(0);

    class LocalConfigLine implements ConfigLine {
      private int listIndex;

      public LocalConfigLine(int listIndex) {
        this.listIndex = listIndex;
      }

      private int index() {
        return isList ? listIndex : 0;
      }

      @Override
      public String fullName() {
        return topFieldName + (isList ? "." + listIndex : "");
      }

      @Override
      public List<ConfigLine> setStoredValue(String strValue, boolean commented) {
        d.markStored(fullName());
        if (!commented) try {
          d.setValue(index(), ConfUtil.convertToType(strValue, type));
        } catch (ConvertingError ignore) {}
        return Collections.emptyList();
      }

      @Override
      public boolean isStored() {
        return d.isStored(fullName());
      }

      @Override
      public String description() {
        return description;
      }

      @Override
      public String getNotNullDefaultStringValue() {
        return defaultValue == null ? "" : "" + defaultValue;
      }
    }

    ReadElement readElement = new ReadElement() {
      @Override
      public String fieldName() {
        return topFieldName;
      }

      @Override
      public Object fieldValue() {
        return isList ? d.listValue() : d.firstValue();
      }
    };

    List<ConfigLine> configLineList = new ArrayList<>();

    if (isList) configLineList.add(new ConfigLine() {
      @Override
      public String fullName() {
        return topFieldName + "." + HotConfigConstants.COUNT_SUFFIX;
      }

      @Override
      public List<ConfigLine> setStoredValue(String value, boolean commented) {
        if (commented) return Collections.emptyList();

        int count = (int) ConfUtil.convertToType(value, int.class);

        d.markStored(fullName());

        List<ConfigLine> ret = new ArrayList<>();

        for (int i = 0; i < count; i++) {
          d.assertExists(i);
          ret.add(new LocalConfigLine(i));
        }

        return ret;
      }

      @Override
      public boolean isStored() {
        return d.isStored(fullName());
      }

      @Override
      public String description() {
        return "Количество элементов в массиве " + topFieldName;
      }

      @Override
      public String getNotNullDefaultStringValue() {
        return "" + defaultListSize;
      }
    });

    {
      int count = defaultListSize == null ? 1 : defaultListSize;
      for (int i = 0; i < count; i++) {
        d.assertExists(i);
        configLineList.add(new LocalConfigLine(i));
      }
    }

    return new LineStructure(Collections.singletonList(readElement), configLineList);
  }

}
