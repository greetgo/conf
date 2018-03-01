package kz.greetgo.conf.type_manager;

import kz.greetgo.conf.hot.LineHibernate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
  public Class<?> type() {
    return type;
  }

  @Override
  public Object extractDefaultValue(Annotation[] annotations, Function<String, String> parameterReplacer) {
    //simple return null, because default value would be get by method #newDefaultValue(Object)
    return null;
  }

  @Override
  public List<LineHibernate> createLineHibernateList(String topFieldName, Object defaultValue, String description) {

    List<LineHibernate> ret = new ArrayList<>();

    Object object = newDefaultValue(defaultValue);
    Object defObject = newDefaultValue(defaultValue);

    for (FieldAcceptor fieldAcceptor : FieldAcceptorCreator.createList(type)) {
      ret.add(new LineHibernate() {
        @Override
        public String fullName() {
          return topFieldName + "." + fieldAcceptor.name();
        }

        boolean hasStored = false;

        @Override
        public void setStoredValue(String strValue, boolean commented) {
          hasStored = true;
          if (!commented) fieldAcceptor.setStrValue(object, strValue);
        }

        @Override
        public boolean isValueSource() {
          return false;
        }

        @Override
        public String fieldName() {
          throw new UnsupportedOperationException();
        }

        @Override
        public Object fieldValue() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasStoredValue() {
          return hasStored;
        }

        @Override
        public boolean hasContent() {
          return true;
        }

        @Override
        public String description() {
          return description;
        }

        @Override
        public String getStringDefaultValue() {
          return fieldAcceptor.getStrValue(defObject);
        }
      });
    }

    ret.add(new LineHibernate() {
      @Override
      public String fullName() {
        return "[" + topFieldName + "]";
      }

      @Override
      public void setStoredValue(String strValue, boolean commented) {
        throw new RuntimeException("Сюда ничего нельзя присваивать");
      }

      @Override
      public boolean isValueSource() {
        return true;
      }

      @Override
      public String fieldName() {
        return topFieldName;
      }

      @Override
      public Object fieldValue() {
        return object;
      }

      @Override
      public boolean hasStoredValue() {
        return false;
      }

      @Override
      public boolean hasContent() {
        return false;
      }

      @Override
      public String description() {
        throw new RuntimeException("Это вызывать нельзя");
      }

      @Override
      public String getStringDefaultValue() {
        throw new RuntimeException("Это вызывать нельзя");
      }
    });

    return ret;
  }
}
