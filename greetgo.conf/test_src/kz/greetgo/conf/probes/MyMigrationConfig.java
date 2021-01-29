package kz.greetgo.conf.probes;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultLongValue;
import kz.greetgo.conf.hot.Description;

@Description("Миграция состояний в ядро системы")
public interface MyMigrationConfig {

  @Description("Максимальное время миграции одной порции в миллисекундах." +
                 " Если оно будет превышено, то миграция будет немедленно прервана с ошибкой")
  @DefaultLongValue(30000)
  long updateTimeoutMs();

  @Description("Размер порции мигрируемых данных")
  @DefaultIntValue(150)
  int batchSize();
}
