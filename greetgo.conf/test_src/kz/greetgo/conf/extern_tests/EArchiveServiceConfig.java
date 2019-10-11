package kz.greetgo.conf.extern_tests;

import kz.greetgo.conf.hot.DefaultBoolValue;
import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры Электронного Архива")
public interface EArchiveServiceConfig {

  @Description("Url для сервиса TIBCO")
  @DefaultStrValue("http://esb-tibco.hq.bc:27745/EArchive_DocsByRef_Get_Ws")
  String url();

  @Description("Имя системы от которого отправляется запрос")
  @DefaultStrValue("AIS_PTP")
  String callSystem();

  @Description("Использовать фэйк вместо сервиса")
  @DefaultBoolValue(true)
  boolean useFake();

  @Description("Таймаут для коннекшина")
  @DefaultIntValue(5000)
  int getConnectionTimeout();

  @Description("Таймаут для получения данных")
  @DefaultIntValue(5000)
  int getReceiveTimeout();

}
