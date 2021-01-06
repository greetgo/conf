package kz.greetgo.conf;

import java.util.Random;

public class RND {
  public static final Random rnd = new Random();

  public static final String RUS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
  public static final String rus = RUS.toLowerCase();
  @SuppressWarnings("SpellCheckingInspection")
  public static final String ENG = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String eng = ENG.toLowerCase();
  public static final String DEG = "0123456789";

  public static final String ALL           = RUS + rus + ENG + eng + DEG;
  public static final char[] ALL_CHARS     = ALL.toCharArray();
  public static final int    ALL_CHARS_LEN = ALL_CHARS.length;

  public static String str(int len) {
    char[] ret = new char[len];

    for (int i = 0; i < len; i++) {
      ret[i] = ALL_CHARS[rnd.nextInt(ALL_CHARS_LEN)];
    }

    return new String(ret);
  }

  public static int intOf(int minValue, int maxValue) {
    return minValue + rnd.nextInt(maxValue - minValue);
  }

  public static long longOf(long minValue, long maxValue) {
    return minValue + Math.abs(rnd.nextLong()) % (maxValue - minValue);
  }
}
