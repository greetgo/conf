package kz.greetgo.conf.env;

import kz.greetgo.conf.RND;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static org.assertj.core.api.Assertions.assertThat;

public class FileParamReaderTest {

  @Test
  public void readParam__createFile() throws IOException {

    Path configFile = Paths.get("build").resolve("FileParamReaderTest").resolve("ConfigFile01.txt");
    deleteIfExists(configFile);

    String fileDescription1 = RND.str(10);
    String fileDescription2 = RND.str(10);
    String fileDescription3 = RND.str(10);

    FileParamReader fileParamReader = new FileParamReader(configFile,
                                                          fileDescription1 + "\n" + fileDescription2 + "\n" + fileDescription3);

    String paramDescription1 = RND.str(10);
    String paramDescription2 = RND.str(10);
    String paramDescription3 = RND.str(10);

    //
    //
    String param = fileParamReader.readParam("servers", "localhost:27017",
                                             paramDescription1 + '\n' + paramDescription2 + '\n' + paramDescription3);
    //
    //

    assertThat(param).isEqualTo("localhost:27017");

    assertThat(configFile).exists();

    boolean existsFileDescription1  = false;
    boolean existsFileDescription2  = false;
    boolean existsFileDescription3  = false;
    boolean existsParamDescription1 = false;
    boolean existsParamDescription2 = false;
    boolean existsParamDescription3 = false;

    for (String line : Files.readAllLines(configFile)) {
      String trimmedLine = line.trim();
      if (!trimmedLine.startsWith("#")) {
        continue;
      }

      if (line.contains(fileDescription1)) {
        existsFileDescription1 = true;
        continue;
      }
      if (line.contains(fileDescription2)) {
        existsFileDescription2 = true;
        continue;
      }
      if (line.contains(fileDescription3)) {
        existsFileDescription3 = true;
        continue;
      }
      if (line.contains(paramDescription1)) {
        existsParamDescription1 = true;
        continue;
      }
      if (line.contains(paramDescription2)) {
        existsParamDescription2 = true;
        continue;
      }
      if (line.contains(paramDescription3)) {
        existsParamDescription3 = true;
        continue;
      }

      if (line.contains("servers")) {
        assertThat(line).contains("localhost:27017");
        continue;
      }

      throw new RuntimeException("I8LNo17zfN :: какая-от непонятная строка [[" + line + "]]");
    }

    assertThat(existsFileDescription1).isTrue();
    assertThat(existsFileDescription2).isTrue();
    assertThat(existsFileDescription3).isTrue();
    assertThat(existsParamDescription1).isTrue();
    assertThat(existsParamDescription2).isTrue();
    assertThat(existsParamDescription3).isTrue();
  }

  @Test
  public void readParam__readFromFile() throws IOException {

    StringBuilder fileContent = new StringBuilder();
    fileContent.append(" # some data\n");
    fileContent.append(" # some info\n");
    fileContent.append("\n");
    fileContent.append("\n");
    fileContent.append("# Params\n");
    fileContent.append("servers = system_data:WOW hello \n");
    fileContent.append("\n");
    fileContent.append("# Нарзан\n");

    Path configFile = Paths.get("build").resolve("FileParamReaderTest").resolve("ConfigFile02.txt");
    deleteIfExists(configFile);
    //noinspection ResultOfMethodCallIgnored
    configFile.toFile().getParentFile().mkdirs();
    Files.write(configFile, fileContent.toString().getBytes(UTF_8));

    FileParamReader fileParamReader = new FileParamReader(configFile, null);

    //
    //
    String param = fileParamReader.readParam("servers", "localhost:27017", null);
    //
    //

    assertThat(param).isEqualTo("system_data:WOW hello");

    assertThat(configFile).exists();

    String fileContent2 = new String(Files.readAllBytes(configFile), UTF_8);

    assertThat(fileContent2).isEqualTo(fileContent.toString());

  }

  @Test
  public void readParam__fileExists__butParameterCommented() throws IOException {

    StringBuilder fileContent = new StringBuilder();
    fileContent.append(" # some data\n");
    fileContent.append(" # some info\n");
    fileContent.append("\n");
    fileContent.append("\n");
    fileContent.append("# Params\n");
    fileContent.append("#servers = system_data:WOW hello \n");
    fileContent.append("\n");
    fileContent.append("# Нарзан\n");

    Path configFile = Paths.get("build").resolve("FileParamReaderTest").resolve("ConfigFile03.txt");
    deleteIfExists(configFile);
    //noinspection ResultOfMethodCallIgnored
    configFile.toFile().getParentFile().mkdirs();
    Files.write(configFile, fileContent.toString().getBytes(UTF_8));

    FileParamReader fileParamReader = new FileParamReader(configFile, null);

    //
    //
    String param = fileParamReader.readParam("servers", "localhost:27017", null);
    //
    //

    assertThat(param).isEqualTo("localhost:27017");

    assertThat(configFile).exists();

    String fileContent2 = new String(Files.readAllBytes(configFile), UTF_8);

    assertThat(fileContent2).isEqualTo(fileContent.toString());

  }

  @Test
  public void readParam__fileExists__butParameterIsAbsent() throws IOException {

    StringBuilder fileContent = new StringBuilder();
    fileContent.append(" # some data\n");
    fileContent.append(" # some info\n");
    fileContent.append("\n");
    fileContent.append("\n");

    Path configFile = Paths.get("build").resolve("FileParamReaderTest").resolve("ConfigFile04.txt");
    deleteIfExists(configFile);
    //noinspection ResultOfMethodCallIgnored
    configFile.toFile().getParentFile().mkdirs();
    Files.write(configFile, fileContent.toString().getBytes(UTF_8));

    FileParamReader fileParamReader = new FileParamReader(configFile, null);

    String paramDescription1 = RND.str(10);
    String paramDescription2 = RND.str(10);
    String paramDescription3 = RND.str(10);

    //
    //
    String param = fileParamReader.readParam("servers", "localhost:27017",
                                             paramDescription1 + '\n' + paramDescription2 + '\n' + paramDescription3);
    //
    //

    assertThat(param).isEqualTo("localhost:27017");

    assertThat(configFile).exists();

    String fileContent2 = new String(Files.readAllBytes(configFile), UTF_8);

    assertThat(fileContent2).isNotEqualTo(fileContent.toString());
    assertThat(fileContent2).contains(paramDescription1);
    assertThat(fileContent2).contains(paramDescription2);
    assertThat(fileContent2).contains(paramDescription3);

  }

}
