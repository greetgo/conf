package kz.greetgo.conf.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ConfAccessFile implements ConfAccess {

  private final File                  file;
  private final ConfContentSerializer contentSerializer;

  public ConfAccessFile(File file, ConfContentSerializer contentSerializer) {
    Objects.requireNonNull(file);
    Objects.requireNonNull(contentSerializer);
    this.file              = file;
    this.contentSerializer = contentSerializer;
  }

  @Override
  public ConfContent load() {
    if (!file.exists()) {
      return contentSerializer.deserialize(null);
    }
    try {
      String text = new String(Files.readAllBytes(file.toPath()), UTF_8);
      return contentSerializer.deserialize(text);
    } catch (NoSuchFileException e) {
      return contentSerializer.deserialize(null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(ConfContent confContent) {
    String text = contentSerializer.serialize(confContent);
    if (text == null) {
      //noinspection ResultOfMethodCallIgnored
      file.delete();
      return;
    }
    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();
    try {
      Files.write(file.toPath(), text.getBytes(UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Date lastModifiedAt() {
    if (!file.exists()) {
      return null;
    }
    try {
      FileTime fileTime = Files.getLastModifiedTime(file.toPath());
      return new Date(fileTime.toMillis());
    } catch (java.nio.file.NoSuchFileException e) {
      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
