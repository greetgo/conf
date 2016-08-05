package kz.greetgo.conf.hot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HotConfigDefinitionImpl implements HotConfigDefinition {

  private final List<HotElementDefinition> elementDefinitions = new ArrayList<>();
  private final String location;

  public HotConfigDefinitionImpl(String location, List<HotElementDefinition> elementDefinitions) {
    this.location = location;
    this.elementDefinitions.addAll(elementDefinitions);

  }

  @Override
  public String location() {
    return location;
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
