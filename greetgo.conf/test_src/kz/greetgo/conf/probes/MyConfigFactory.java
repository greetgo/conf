package kz.greetgo.conf.probes;

import kz.greetgo.conf.hot.FileConfigFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MyConfigFactory extends FileConfigFactory {
  @Override
  protected Path getBaseDir() {
    return Paths.get("build/" + getClass().getSimpleName());
  }

  @Override
  protected String getConfigFileExt() {
    return ".conf";
  }
}
