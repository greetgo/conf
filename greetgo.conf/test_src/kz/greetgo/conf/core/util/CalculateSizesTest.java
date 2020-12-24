package kz.greetgo.conf.core.util;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class CalculateSizesTest {
  @Test
  public void of() {

    Set<String> keys = new HashSet<>();
    keys.add("hello.3");
    keys.add("hello.7");
    keys.add("amino.wow.3.status.7.x");
    keys.add("amino.wow.3.status.8.x");
    keys.add("amino.wow.1.status.3.x");
    keys.add("amino.wow.1.status.2.z");
    keys.add("amino.wow.1.status.2.round.1");
    keys.add("amino.wow.1.status.2.round.2");
    keys.add("amino.wow.1.status.2.round.9");

    //
    //
    Map<String, Integer> sizes = CalculateSizes.of(keys);
    //
    //

    assertThat(sizes).contains(entry("hello", 8));
    assertThat(sizes).contains(entry("amino.wow", 4));
    assertThat(sizes).contains(entry("amino.wow.3.status", 9));
    assertThat(sizes).contains(entry("amino.wow.1.status", 4));
    assertThat(sizes).contains(entry("amino.wow.1.status.2.round", 10));
  }
}
