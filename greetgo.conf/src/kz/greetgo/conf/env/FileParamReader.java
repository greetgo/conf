package kz.greetgo.conf.env;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileParamReader {

  private final Path   configFile;
  private final String fileDescription;

  public FileParamReader(Path configFile, String fileDescription) {
    this.configFile      = configFile;
    this.fileDescription = fileDescription;
  }

  public String readParam(String paramName, String defaultValue, String paramDescription) {

    if (!Files.exists(configFile)) {

      StringBuilder sb = new StringBuilder();
      if (fileDescription != null) {
        sb.append('\n');
        for (String line : fileDescription.split("\n")) {
          sb.append("# ").append(line).append('\n');
        }
      }

      appendParam(sb, paramName, defaultValue, paramDescription);

      //noinspection ResultOfMethodCallIgnored
      configFile.toFile().getParentFile().mkdirs();
      try {
        Files.write(configFile, sb.toString().getBytes(UTF_8));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      return defaultValue;
    }


    try {
      Pattern pattern = Pattern.compile("\\s*(#\\s*)?" + paramName + "\\s*=(.*)");
      boolean exists  = false;
      String  value   = null;
      for (String line : Files.readAllLines(configFile)) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
          exists = true;

          if (matcher.group(1) == null) {
            value = matcher.group(2).trim();
          }

          break;
        }
      }

      if (!exists) {

        StringBuilder sb = new StringBuilder();
        appendParam(sb, paramName, defaultValue, paramDescription);

        try (OutputStream fileOutputStream = new FileOutputStream(configFile.toFile(), true)) {
          fileOutputStream.write(sb.toString().getBytes(UTF_8));
        }

      }

      return value != null ? value : defaultValue;

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  protected void appendParam(StringBuilder sb, String paramName, String defaultValue, String paramDescription) {
    sb.append('\n');
    if (paramDescription != null) {
      for (String line : paramDescription.split("\n")) {
        sb.append("# ").append(line).append('\n');
      }
      sb.append('#');
      sb.append(paramName).append('=').append(defaultValue);
    }
  }

}
