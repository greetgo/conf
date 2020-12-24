package kz.greetgo.conf.core.util;

import kz.greetgo.conf.hot.Description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnionDescriptions {

  public static List<String> unionDescriptions(Description... descriptions) {
    List<String> ret = new ArrayList<>();

    boolean needSpace = false;

    for (Description description : descriptions) {
      if (description != null) {

        if (needSpace) {
          ret.add("");
        } else {
          needSpace = true;
        }

        ret.addAll(Arrays.asList(description.value().split("\n")));

      }
    }

    return ret;
  }

}
