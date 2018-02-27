package kz.greetgo.conf.hot;

import java.util.Collections;
import java.util.List;

public class HotConfigDefinitionModel implements HotConfigDefinition {

  private final List<ElementDefinition> elementDefinitions;
  private final String location, description;

  public HotConfigDefinitionModel(String location, String description, List<ElementDefinition> elementDefinitions) {
    this.location = location;
    this.description = description;
    this.elementDefinitions = Collections.unmodifiableList(elementDefinitions);
  }

  @Override
  public String location() {
    return location;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public List<ElementDefinition> elementList() {
    return elementDefinitions;
  }
}
