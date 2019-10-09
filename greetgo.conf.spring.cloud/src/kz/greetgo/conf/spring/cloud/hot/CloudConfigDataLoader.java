package kz.greetgo.conf.spring.cloud.hot;

import kz.greetgo.conf.hot.*;

import java.util.Date;
import java.util.Map;

public class CloudConfigDataLoader extends ConfigDataLoader {

  public static void loadConfigDataToCloud(Map<String, Object> target,
                                           HotConfigDefinition configDefinition,
                                           ConfigStorage configStorage, Date now) {

    try {
      new CloudConfigDataLoader(target, configDefinition, configStorage, now).loadCloud();
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }

  }

  private CloudConfigDataLoader(Map<String, Object> target, HotConfigDefinition configDefinition,
                                ConfigStorage configStorage, Date now) {
    super(target, configDefinition,configStorage, now);
  }

  private void loadCloud() throws Exception {

    for (ElementDefinition ed : this.configDefinition.elementList()) {
      loadingLines.putCloudDefinition(configDefinition.configInterface(), ed);
    }

    loadingLines.readCloudContent(((CloudConfigStorage)configStorage).loadCloudConfigContent());

    loadingLines.saveTo(target);
  }

}
