package kz.greetgo.conf.hot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HotConfigDefinitionModel implements HotConfigDefinition {

  private final List<HotElementDefinition> elementDefinitions = new ArrayList<>();
  private final String location, description;

  public HotConfigDefinitionModel(String location, String description, List<HotElementDefinition> elementDefinitions) {
    this.location = location;
    this.description = description;
    this.elementDefinitions.addAll(elementDefinitions);
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
  public int elementCount() {
    return elementDefinitions.size();
  }

  @Override
  public HotElementDefinition element(int index) {
    return elementDefinitions.get(index);
  }

  @Override
  public Iterator<HotElementDefinition> iterator() {
    return elementDefinitions.iterator();
  }
}
