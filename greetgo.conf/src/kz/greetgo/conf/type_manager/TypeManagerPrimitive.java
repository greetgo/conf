package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.ConfUtil;
import kz.greetgo.conf.hot.ConfigLine;
import kz.greetgo.conf.hot.ConvertingError;
import kz.greetgo.conf.hot.LineStructure;
import kz.greetgo.conf.hot.ReadElement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  public LineStructure createLineStructure(String topFieldName, Object defaultValue, String description, boolean isList) {
    List<ReadElement> readElementList = new ArrayList<>();
    List<ConfigLine> configLineList = new ArrayList<>();

    class Data {
      List<Object> list = new ArrayList<>();

      void setValue(int index, Object value) {
        assertExists(index);
        list.set(index, value);
      }

      private void assertExists(int index) {
        while (list.size() <= index) {
          list.add(defaultValue);
        }
      }

      List list() {
        return new ArrayList<>(list);
      }

      Object firstValue() {
        if (list.size() == 0) return defaultValue;
        return list.get(0);
      }
    }

    final Data d = new Data();

    class LocalConfigLine implements ConfigLine {
      boolean isStored = false;
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
        isStored = true;
        if (!commented) try {
          d.setValue(index(), ConfUtil.convertToType(strValue, type));
        } catch (ConvertingError ignore) {}
        return Collections.emptyList();
      }

      @Override
      public boolean isStored() {
        return isStored;
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

    readElementList.add(new ReadElement() {
      @Override
      public String fieldName() {
        return topFieldName;
      }

      @Override
      public Object fieldValue() {
        return isList ? d.list() : d.firstValue();
      }

      @Override
      public List<ConfigLine> createListConfigLines(int index) {
        return Collections.singletonList(new LocalConfigLine(index));
      }
    });

    if (!isList) configLineList.add(new LocalConfigLine(0));

    return new LineStructure(readElementList, configLineList);
  }
}
