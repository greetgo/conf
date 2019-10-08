package kz.greetgo.conf.in_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CloudPropertyModel {
  public String name;
  public String label;
  public String state;
  public String version;
  public List<String> profiles = new ArrayList<>();
  public List<PropertySource> propertySources = new ArrayList<>();
}
