package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.ConfUtil;
import kz.greetgo.conf.hot.ConfigLine;
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
import java.util.stream.Collectors;

import static kz.greetgo.conf.ConfUtil.concatNewLine;
import static kz.greetgo.conf.ConfUtil.nullToEmpty;

public class TypeManagerClass implements TypeManager {

  public final Class<?> type;

  public TypeManagerClass(Class<?> type) {
    this.type = type;
  }

  @Override
  public Object newDefaultValue(Object defaultValue) {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object extractDefaultValue(Annotation[] annotations, Function<String, String> parameterReplacer) {
    //simple return null, because default value would be get by method #newDefaultValue(Object)
    return null;
  }

  @Override
  public Object fromStr(String strValue) {
    throw new RuntimeException("Cannot convert from string `" + strValue + "` to type " + type);
  }

  @Override
  public LineStructure createLineStructure(String topFieldName, Object defaultValue,
                                           String description, Integer defaultListSize) {

    boolean isList = defaultListSize != null;

    final Object defObject = newDefaultValue(defaultValue);

    class Data {
      final List<Object> list = new ArrayList<>();

      Object getOrCreate(int index) {
        while (index >= list.size()) {
          list.add(newDefaultValue(defaultValue));
        }
        return list.get(index);
      }

      List<Object> listValue() {
        return new ArrayList<>(list);
      }

      final Set<String> storeIsTrue = new HashSet<>();

      void markStored(String fullName) {
        storeIsTrue.add(fullName);
      }

      boolean checkStored(String fullName) {
        return storeIsTrue.contains(fullName);
      }

      public List<ConfigLine> createListConfigLines(int index) {
        getOrCreate(index);
        return FieldAcceptorCreator.createList(type).stream().map(fieldAcceptor -> new ConfigLine() {

          @Override
          public String fullName() {
            return isList
              ? topFieldName + "." + index + "." + fieldAcceptor.name()
              : topFieldName + "." + fieldAcceptor.name();
          }

          @Override
          public List<ConfigLine> setStoredValue(String strValue, boolean commented) {
            markStored(fullName());
            if (!commented) fieldAcceptor.setStrValue(getOrCreate(isList ? index : 0), strValue);
            return Collections.emptyList();
          }

          @Override
          public boolean isStored() {
            return checkStored(fullName());
          }

          @Override
          public String description() {
            return concatNewLine(description, fieldAcceptor.description());
          }

          @Override
          public String getNotNullDefaultStringValue() {
            return nullToEmpty(fieldAcceptor.getStrValue(defObject));
          }
        }).collect(Collectors.toList());
      }

    }

    final Data d = new Data();

    d.getOrCreate(0);

    final ReadElement readElement = new ReadElement() {
      @Override
      public String fieldName() {
        return topFieldName;
      }

      @Override
      public Object fieldValue() {
        return isList ? d.listValue() : d.getOrCreate(0);
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
        if (commented) return null;
        int count = (int) ConfUtil.convertToType(value, int.class);
        d.markStored(fullName());

        List<ConfigLine> ret = new ArrayList<>();

        for (int i = 0; i < count; i++) {
          ret.addAll(d.createListConfigLines(i));
        }

        return ret;
      }

      @Override
      public boolean isStored() {
        return d.checkStored(fullName());
      }

      @Override
      public String description() {
        return "Количество элементов в " + topFieldName;
      }

      @Override
      public String getNotNullDefaultStringValue() {
        return "" + defaultListSize;
      }
    });

    {
      int count = defaultListSize == null ? 1 : defaultListSize;
      for (int i = 0; i < count; i++) {
        configLineList.addAll(d.createListConfigLines(i));
      }
    }

    return new LineStructure(
      Collections.singletonList(readElement),
      configLineList
    );
  }

}
