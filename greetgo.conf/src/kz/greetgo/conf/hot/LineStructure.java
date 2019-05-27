package kz.greetgo.conf.hot;

import java.util.List;

public class LineStructure {

  public final List<ReadElement> readElementList;
  public final List<ConfigLine> configLineList;

  public LineStructure(List<ReadElement> readElementList, List<ConfigLine> configLineList) {
    this.readElementList = readElementList;
    this.configLineList = configLineList;
  }

}
