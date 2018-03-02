package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.hot.ConfigLine;
import kz.greetgo.conf.hot.LineStructure;
import kz.greetgo.conf.hot.ReadElement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
  public LineStructure createLineStructure(String topFieldName, Object defaultValue, String description) {
    List<ReadElement> readElementList = new ArrayList<>();

    final Object object[] = new Object[]{newDefaultValue(defaultValue)};
    final Object defObject = newDefaultValue(defaultValue);

    readElementList.add(new ReadElement() {
      @Override
      public String fieldName() {
        return topFieldName;
      }

      @Override
      public Object fieldValue() {
        return object[0];
      }
    });

    List<ConfigLine> configLineList = new ArrayList<>();

    for (FieldAcceptor fieldAcceptor : FieldAcceptorCreator.createList(type)) {
      configLineList.add(new ConfigLine() {
        boolean isStored = false;

        @Override
        public String fullName() {
          return topFieldName + "." + fieldAcceptor.name();
        }

        @Override
        public void setStoredValue(String strValue, boolean commented) {
          isStored = true;
          if (!commented) fieldAcceptor.setStrValue(object[0], strValue);
        }

        @Override
        public boolean isStored() {
          return isStored;
        }

        @Override
        public String description() {
          return concatNewLine(description, fieldAcceptor.description());
        }

        @Override
        public String getNotNullDefaultStringValue() {
          return nullToEmpty(fieldAcceptor.getStrValue(defObject));
        }
      });
    }

    return new LineStructure(readElementList, configLineList);
  }
}
