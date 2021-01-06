package kz.greetgo.conf.core;

public interface ConfCallback {

  String readParam(String paramPath);

  int readParamSize(String paramPath);

  String readEnv(String envName);

}
