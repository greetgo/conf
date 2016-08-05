package kz.greetgo.conf.hot;

public class HotElementDefinition {
  public String name;
  public Class<?> type;
  public Object defaultValue;
  public String description;

  public HotElementDefinition() {
  }

  public HotElementDefinition(String name, Class<?> type, Object defaultValue, String description) {
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
    this.description = description;
  }
}
