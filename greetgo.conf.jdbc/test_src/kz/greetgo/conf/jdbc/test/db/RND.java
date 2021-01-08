package kz.greetgo.conf.jdbc.test.db;

import java.util.Random;

public class RND {

  @SuppressWarnings("SpellCheckingInspection")
  public static final String ENG         = "abcdefghijklmnopqrstuvwxyz";
  public static final char[] ENG_ARR     = (ENG.toUpperCase() + ENG.toLowerCase()).toCharArray();
  public static final int    ENG_ARR_LEN = ENG_ARR.length;

  public static final Random rnd = new Random();

  public static String strEng(int len) {
    char[] chars = new char[len];
    for (int i = 0; i < len; i++) {
      chars[i] = ENG_ARR[rnd.nextInt(ENG_ARR_LEN)];
    }
    return new String(chars);
  }

}
