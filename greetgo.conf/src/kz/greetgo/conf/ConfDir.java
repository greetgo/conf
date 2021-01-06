package kz.greetgo.conf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfDir {
  private static final Map<String, String> dirMap = new HashMap<>();

  private ConfDir() {}

  public static String confDir(String sysId) {

    String value = dirMap.get(sysId);

    if (value == null) {
      value = generateConfDir(sysId);
      dirMap.put(sysId, value);
    }

    return value;
  }

  private static String generateConfDir(String sysId) {
    {
      String value = generateSpecificDir(sysId);
      if (value != null) {
        return value;
      }
    }
    return System.getProperty("user.home") + "/" + sysId + ".d";
  }

  private static String generateSpecificDir(String sysId) {
    String userDir = System.getProperty("user.dir");
    if (userDir == null) {
      return null;
    }

    userDir = userDir.toUpperCase().trim();

    File   home  = new File(System.getProperty("user.home"));
    File[] files = home.listFiles();
    if (files == null) {
      return null;
    }
    for (File file : files) {
      if (!file.isDirectory()) {
        continue;
      }
      if (!file.getName().startsWith(sysId + ".")) {
        continue;
      }
      if (!file.getName().endsWith(".d")) {
        continue;
      }

      File idFile = new File(file.getAbsolutePath() + "/id");
      if (!idFile.exists()) {
        continue;
      }

      if (userDir.equals(ConfUtil.readFile(idFile).toUpperCase().trim())) {
        return file.getAbsolutePath();
      }
    }

    return null;
  }

}
