package kz.greetgo.conf.core;

import org.testng.annotations.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfContentTest {

  @Test
  public void minus() {

    ConfContent a = new ConfContent();
    a.records.add(ConfRecord.of("a", ""));
    a.records.add(ConfRecord.of("b", ""));
    a.records.add(ConfRecord.of("c", ""));
    a.records.add(ConfRecord.of("d", ""));

    ConfContent b = new ConfContent();
    b.records.add(ConfRecord.of("c", ""));
    b.records.add(ConfRecord.of("d", ""));
    b.records.add(ConfRecord.of("e", ""));

    //
    //
    ConfContent c = a.minus(b);
    //
    //

    Set<String> cc = c.records.stream().map(ConfRecord::trimmedKey).collect(Collectors.toSet());

    assertThat(cc).contains("a", "b");
    assertThat(cc).doesNotContain("c", "d", "e");
  }

  @Test
  public void minus_trimming() {

    ConfContent a = new ConfContent();
    a.records.add(ConfRecord.of(" a", ""));
    a.records.add(ConfRecord.of("b ", ""));
    a.records.add(ConfRecord.of("  c ", ""));
    a.records.add(ConfRecord.of(" d", ""));

    ConfContent b = new ConfContent();
    b.records.add(ConfRecord.of("c  ", ""));
    b.records.add(ConfRecord.of("   d ", ""));
    b.records.add(ConfRecord.of(" e     ", ""));

    //
    //
    ConfContent c = a.minus(b);
    //
    //

    Set<String> cc = c.records.stream().map(ConfRecord::trimmedKey).collect(Collectors.toSet());

    assertThat(cc).contains("a", "b");
    assertThat(cc).doesNotContain("c", "d", "e");
  }
}
