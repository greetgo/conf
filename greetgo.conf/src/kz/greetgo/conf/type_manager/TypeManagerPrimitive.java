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

  @Override
  public Class<?> type() {
    return type;
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
  public LineStructure createLineStructure(String topFieldName, Object defaultValue, String description) {
    List<ReadElement> readElementList = new ArrayList<>();
    List<ConfigLine> configLineList = new ArrayList<>();

    final Object[] value = new Object[]{defaultValue};

    readElementList.add(new ReadElement() {
      @Override
      public String fieldName() {
        return topFieldName;
      }

      @Override
      public Object fieldValue() {
        return value[0];
      }
    });

    configLineList.add(new ConfigLine() {
      boolean isStored = false;

      @Override
      public String fullName() {
        return topFieldName;
      }

      @Override
      public void setStoredValue(String strValue, boolean commented) {
        isStored = true;
        if (!commented) try {
          value[0] = ConfUtil.convertToType(strValue, type);
        } catch (ConvertingError ignore) {}
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
    });

    return new LineStructure(readElementList, configLineList);
  }
}
