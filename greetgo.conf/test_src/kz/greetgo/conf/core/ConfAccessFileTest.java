package kz.greetgo.conf.core;

import kz.greetgo.conf.RND;
import kz.greetgo.conf.test.util.ThreadWithError;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfAccessFileTest {

  private final Path                  testDir           = Paths.get("build/" + getClass().getSimpleName());
  private final ConfContentSerializer contentSerializer = new ConfAccessStdSerializer();

  @Test
  public void write() {

    ConfContent cc = new ConfContent();
    cc.records.add(ConfRecord.ofComment("Тестовый конфиг"));
    cc.records.add(ConfRecord.of("param1", "value1", "Comment 1"));
    cc.records.add(ConfRecord.of("param2", "value2", "Comment 2"));

    File configFile = testDir.resolve("config-write-" + RND.str(5) + ".txt").toFile();

    ConfAccessFile accessFile = new ConfAccessFile(configFile, contentSerializer);

    //
    //
    accessFile.write(cc);
    //
    //

    assertThat(configFile).exists();
  }

  @Test
  public void read() throws Exception {

    File configFile = testDir.resolve("config-read-" + RND.str(5) + ".txt").toFile();
    configFile.getParentFile().mkdirs();
    Files.write(configFile.toPath(), "#Супер конфиг\n\n#Крутой параметр\npar1=wow1\n".getBytes(UTF_8));

    ConfAccessFile accessFile = new ConfAccessFile(configFile, contentSerializer);

    //
    //
    ConfContent cc = accessFile.load();
    //
    //

    assertThat(cc).isNotNull();
    assertThat(cc.records).isNotEmpty();

    for (ConfRecord record : cc.records) {
      System.out.println("KL4BzN8qSy :: " + record);
    }
  }

  @Test
  public void read__noFile() {

    File configFile = testDir.resolve("config-read-" + RND.str(5) + ".txt").toFile();

    ConfAccessFile accessFile = new ConfAccessFile(configFile, contentSerializer);

    //
    //
    ConfContent cc = accessFile.load();
    //
    //

    assertThat(cc).isNotNull();
    assertThat(cc.records).isEmpty();
  }

  @Test
  public void lastModifiedAt() throws Exception {

    File configFile = testDir.resolve("config-read-" + RND.str(5) + ".txt").toFile();
    configFile.getParentFile().mkdirs();
    Files.write(configFile.toPath(), "#Супер конфиг\n\n#Крутой параметр\npar1=wow1\n".getBytes(UTF_8));

    ConfAccessFile accessFile = new ConfAccessFile(configFile, contentSerializer);

    //
    //
    Date lastModifiedAt = accessFile.lastModifiedAt();
    //
    //

    assertThat(lastModifiedAt).isNotNull();
  }

  @Test
  public void lastModifiedAt__noFile() {

    File configFile = testDir.resolve("config-read-" + RND.str(5) + ".txt").toFile();

    ConfAccessFile accessFile = new ConfAccessFile(configFile, contentSerializer);

    //
    //
    Date lastModifiedAt = accessFile.lastModifiedAt();
    //
    //

    assertThat(lastModifiedAt).isNull();
  }

  @Test(invocationCount = 100)
  public void lastModifiedAt__noFile1() throws Exception {

    File configFile = testDir.resolve("config-read-" + RND.str(5) + ".txt").toFile();
    configFile.getParentFile().mkdirs();
    Files.write(configFile.toPath(), "#Супер конфиг\n\n#Крутой параметр\npar1=wow1\n".getBytes(UTF_8));

    ConfAccessFile accessFile = new ConfAccessFile(configFile, contentSerializer);

    ThreadWithError thread1 = new ThreadWithError(accessFile::lastModifiedAt);
    ThreadWithError thread2 = new ThreadWithError(configFile::delete);


    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();


    thread1.throwIfError();
    thread2.throwIfError();
  }

}
