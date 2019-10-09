package kz.greetgo.conf.spring.cloud;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.conf.spring.cloud.in_service.InServiceUtil;
import kz.greetgo.conf.spring.cloud.in_service.model.CloudPropertyModel;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ConfCloudUtil {
  public static String readCloudFileContent(String baseUrl) {
    return InServiceUtil.getCloudContent(baseUrl);
  }


  public static Map<String, Object> readCloudContent(String baseUrl) {
    String content = readCloudFileContent(baseUrl);
    return convertContentToMap(content);
  }

  public static Map<String, Object> convertContentToMap(String content) {
    if(Objects.isNull(content)) return null;
    try {
      CloudPropertyModel model = new ObjectMapper().readValue(content, CloudPropertyModel.class);
      if(model.propertySources!=null && !model.propertySources.isEmpty()) {
         return model.propertySources.get(0).source;
      }
      return null;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }
}
