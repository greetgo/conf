package kz.greetgo.conf.in_service.model;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public class PropertySource {
  public String name;
  public Map<String, Object> source = new HashMap<>();
}
