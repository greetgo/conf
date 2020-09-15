package kz.greetgo.conf.env;

public interface EnvSource {

  String getValue(String name);

  EnvSource SYSTEM = System::getenv;

}
