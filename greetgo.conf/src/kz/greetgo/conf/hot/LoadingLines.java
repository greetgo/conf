package kz.greetgo.conf.hot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class LoadingLines {
  private final Date now;
  private final String fileDescription;
  private boolean contentExists;

  LoadingLines(Date now, String fileDescription) {
    this.now = now;
    this.fileDescription = fileDescription;
  }

  final LinkedHashMap<String, ConfigLine> configLineMap = new LinkedHashMap<>();
  final List<ReadElement> readElementList = new ArrayList<>();

  private final List<String> realLines = new ArrayList<>();

  public void putDefinition(ElementDefinition ed) {

    LineStructure lineStructure = ed.createLineStructure();

    for (ConfigLine configLine : lineStructure.configLineList) {
      configLineMap.put(configLine.fullName(), configLine);
    }

    readElementList.addAll(lineStructure.readElementList);
  }

  public void readStorageLine(String line) {
    realLines.add(line);

    int index = line.indexOf('=');

    if (index >= 0) {
      String key = line.substring(0, index).trim();
      boolean commented = key.startsWith("#");
      if (commented) key = key.substring(1).trim();
      String value = line.substring(index + 1).trim();

      readStorageLine(key, value, commented);
    }
  }

  private void readStorageLine(String key, String value, boolean commented) {
    ConfigLine line = configLineMap.get(key);
    if (line == null) return;

    line.setStoredValue(value, commented);
  }

  public void saveTo(Map<String, Object> target) {
    for (ReadElement element : readElementList) {
      target.put(element.fieldName(), element.fieldValue());
    }
  }

  public boolean needToSave() {
    if (!contentExists) return configLineMap.size() > 0;
    if (configLineMap.size() == 0) return false;

    for (ConfigLine line : configLineMap.values()) {
      if (!line.isStored()) return true;
    }

    return false;
  }

  static void killedLastEmptyLines(List<String> lines) {
    while (lines.size() > 0) {
      String lastLine = lines.get(lines.size() - 1);
      if (lastLine.trim().length() > 0) return;
      lines.remove(lines.size() - 1);
    }
  }

  public void setContentExists(boolean contentExists) {
    this.contentExists = contentExists;
  }

  private boolean needExpand() {
    for (ConfigLine line : configLineMap.values()) {
      if (!line.isStored()) return true;
    }
    return false;
  }

  public String content() {

    List<String> lines = new ArrayList<>(realLines);

    if (needExpand()) expand(lines);

    return lines.stream().collect(Collectors.joining("\n", "", "\n"));

  }

  private void expand(List<String> lines) {
    killedLastEmptyLines(lines);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    lines.add("");
    lines.add("#");
    lines.add("# " + (contentExists ? "Added" : "Created") + " at " + sdf.format(now));
    lines.add("#");

    if (!contentExists && fileDescription != null) {
      for (String s : fileDescription.split("\n")) {
        lines.add("# " + s.trim());
      }
      lines.add("#");
    }

    for (ConfigLine line : configLineMap.values()) {

      if (!line.isStored()) {
        lines.add("");
        String description = line.description();
        if (description != null) for (String s : description.split("\n")) {
          lines.add("# " + s.trim());
        }
        lines.add(line.fullName() + "=" + line.getNotNullDefaultStringValue());
      }

    }
  }
}
