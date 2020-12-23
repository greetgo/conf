package kz.greetgo.conf.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

public class Delegator<T> implements InvocationHandler {

  private final AtomicReference<T> value = new AtomicReference<>();
  private final T proxy;

  public Delegator(Class<T> delegatingInterface, T initValue) {
    value.set(initValue);
    //noinspection unchecked
    proxy = (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{delegatingInterface}, this);
  }

  public void setNewValue(T value) {
    this.value.set(value);
  }

  public T proxy() {
    return proxy;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return method.invoke(value.get(), args);
  }

}
