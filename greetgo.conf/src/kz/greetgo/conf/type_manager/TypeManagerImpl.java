package kz.greetgo.conf.type_manager;

public class TypeManagerImpl<T> implements TypeManager<T> {
  private final Class<T> managedClass;

  public TypeManagerImpl(Class<T> managedClass) {
    this.managedClass = managedClass;
  }


  @Override
  public T newDefaultValue() {
    try {
      return managedClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
