package kz.greetgo.conf.spring.cloud.hot;

import kz.greetgo.conf.hot.AbstractConfigFactory;
import kz.greetgo.conf.hot.ConfigStorage;

import java.util.Date;
import java.util.Map;

/**
 * Config storage interface. Is is used by library to store config data. You can redefine it to store config any where.
 * It is used in {@link AbstractConfigFactory} to access to config storing data.
 */
public interface CloudConfigStorage extends ConfigStorage {

  /**
   * Loads and returns config content from cloud
   *
   * @return config content
   */
  Map<String, Object> loadCloudConfigContent() throws Exception;

}
