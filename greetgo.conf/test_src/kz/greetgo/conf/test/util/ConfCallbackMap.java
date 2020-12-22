package kz.greetgo.conf.test.util;

import kz.greetgo.conf.core.ConfCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfCallbackMap implements ConfCallback {

  private final Map<String, String> params = new HashMap<>();
  private final Map<String, Integer> sizes = new HashMap<>();
  private final Set<String> addedParams = new HashSet<>();
  private final Set<String> addedSizes = new HashSet<>();

  public void clear() {
    params.clear();
    sizes.clear();
    addedParams.clear();
    addedSizes.clear();
  }

  public void prm(String path, String value) {
    params.put(path, value);
    addedParams.add(path);
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


}
