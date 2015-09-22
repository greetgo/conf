package kz.greetgo.conf;

public class ConfDirProbe {
  public static void main(String[] args) {
    System.out.println("user.dir = " + System.getProperty("user.dir"));
    String confDir = ConfDir.confDir("gccenter");
    System.out.println("confDir = " + confDir);
  }
}
