package kz.greetgo.conf.spring.cloud.in_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.conf.spring.cloud.in_service.model.CloudPropertyModel;
import kz.greetgo.conf.spring.cloud.in_service.model.Response;
import kz.greetgo.conf.spring.cloud.in_service.util.ExceptionUtils;
import lombok.var;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Objects;

public class InServiceUtil {

  public static void main(String[] args) {
    System.out.println(getCloudContent("http://localhost:8888/test-config/master"));
    CloudPropertyModel model = getCloudModel("http://localhost:8888/test-config/master");
    System.out.println(model.toString());
  }

  private static OkHttpClient client;

  public static OkHttpClient getClient() {
    if(client!=null) return client;
    synchronized (InServiceUtil.class) {
      if(client!=null) return client;
      client = new OkHttpClient();
      return client;
    }
  }

  public static CloudPropertyModel getCloudModel(String baseUrl) {
    try {
      String cloudContent = getCloudContent(baseUrl);
      if(Objects.isNull(cloudContent)) return null;
      return new ObjectMapper().readValue(cloudContent, CloudPropertyModel.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getCloudContent(String baseUrl) {
    HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder();

    var request = new Request.Builder()
        .url(builder.build())
        .build();


    Response<String> stringResponse = doRequest(request);
    if(stringResponse.isOk)
      return stringResponse.body;
    return null;
  }

  private static Response<String> doRequest(Request request) {
    var response = new Response<String>();
    try {
      response.url = request.url().toString();
      try (var resp = getClient().newCall(request).execute()) {
        response.body = resp.body().string();
        response.isOk = resp.isSuccessful();
        response.status = resp.code();
        return response;
      }
    } catch (Exception e) {
      response.isOk = false;
      response.body = ExceptionUtils.stackTraceToString(e);
      response.status = 500;
      System.out.println(e.getMessage());
      return response;
    }
  }

  protected static MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");

  protected static MediaType TEXT
      = MediaType.parse("application/json; charset=utf-8");

}
