package kz.greetgo.conf.in_service.model;

import lombok.ToString;

import java.util.Objects;
import java.util.function.Function;

@ToString
public class Response<T> {

  public String url;

  public boolean isOk;

  public T body;

  public int status;

  public Response() {
  }

  public Response(String url, boolean isOk, int status, T body) {
    this.url = url;
    this.isOk = isOk;
    this.body = body;
    this.status = status;
  }

  public static Response error(Object body) {
    Response<String> response = new Response<>();
    response.isOk = false;
    if (Objects.nonNull(body)) {
      response.body = body.toString();
    }
    response.status = 400;
    return response;
  }

  public <R> Response<R> transformBody(R body) {
    return new Response<>(this.url, this.isOk, this.status, body);
  }

  public <R> Response<R> transformBody(Function<T, R> transform) {
    return new Response<>(this.url, this.isOk, this.status, transform.apply(this.body));
  }

}
