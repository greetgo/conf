package kz.greetgo.conf.test.util;

import kz.greetgo.conf.core.ConfCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfCallbackMap implements ConfCallback {

  private final Map<String, String>  params       = new HashMap<>();
  private final Map<String, String>  env          = new HashMap<>();
  private final Map<String, Integer> sizes        = new HashMap<>();
  private final Set<String>          addedParams  = new HashSet<>();
  private final Set<String>          addedSizes   = new HashSet<>();
  private final Set<String>          addedEnvName = new HashSet<>();

  public void clear() {
    params.clear();
    sizes.clear();
    addedParams.clear();
    addedSizes.clear();
    env.clear();
    addedEnvName.clear();
  }

  public void prm(String path, String value) {
    params.put(path, value);
    addedParams.add(path);
  }

  public void env(String envName, String envValue) {
    addedEnvName.add(envName);
    env.put(envName, envValue);
  }

  public void siz(String path, int size) {
    sizes.put(path, size);
    addedSizes.add(path);
  }

  @Override
  public String readParam(String paramPath) {
    if (!addedParams.contains(paramPath)) {
      throw new RuntimeException("Lg5dMtR94m :: no param " + paramPath);
    }
    return params.get(paramPath);
  }

  @Override
  public int readParamSize(String paramPath) {
    if (!addedSizes.contains(paramPath)) {
      throw new RuntimeException("Hoe3t7JEi3 :: no size " + paramPath);
    }
    return sizes.get(paramPath);
  }

  @Override
  public String readEnv(String envName) {
    if (!addedEnvName.contains(envName)) {
      throw new RuntimeException("kHO7C78NLQ :: no env " + envName);
    }
    return env.get(envName);
  }
}
