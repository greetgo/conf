package kz.greetgo.conf.core;

public interface ConfContentSerializer {

  String serialize(ConfContent confContent);

  ConfContent deserialize(String text);

}
