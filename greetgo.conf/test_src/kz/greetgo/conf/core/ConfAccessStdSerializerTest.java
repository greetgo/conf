package kz.greetgo.conf.core;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfAccessStdSerializerTest {

  ConfContentSerializer serializer = new ConfAccessStdSerializer();

  @Test
  public void deserialize() {

    String text = ""
                    + "\n"
                    + "# Common Header1\n"
                    + "# Common Header2\n"
                    + "#\n"
                    + "#   Common Header3\n"
                    + "# Common Header4\n"
                    + "\n"
                    + "# Left comment 1\n"
                    + "# Left comment 2\n"
                    + "\n"
                    + "\n"
                    + "# Comment 1 to param 1\n"
                    + "# Comment 2 to param 1\n"
                    + "param1=value1\n"
                    + "# Comment 1 to param 2\n"
                    + "# Comment 2 to param 2\n"
                    + "# Comment 3 to param 2\n"
                    + "param2=value2\n"
                    + "\n"
                    + "# Left comment 3\n"
                    + "# Left comment 4\n"
                    + "\n"
                    + "# Comment 1 to param 3\n"
                    + "# Comment 2 to param 3\n"
                    + "param3=value3\n"
                    + "\n"
                    + "\n"
                    + "\n";

    //
    //
    ConfContent cc = serializer.deserialize(text);
    //
    //

    assertThat(cc.records).isNotNull();

    assertThat(cc.records.get(0).trimmedKey()).isNull();
    assertThat(cc.records.get(0).trimmedValue()).isNull();
    assertThat(cc.records.get(0).comments)
      .isEqualTo(asList("Common Header1", "Common Header2", "", "  Common Header3", "Common Header4"));

    assertThat(cc.records.get(1).trimmedKey()).isNull();
    assertThat(cc.records.get(1).trimmedValue()).isNull();
    assertThat(cc.records.get(1).comments).isEqualTo(asList("Left comment 1", "Left comment 2"));

    assertThat(cc.records.get(2).trimmedKey()).isEqualTo("param1");
    assertThat(cc.records.get(2).trimmedValue()).isEqualTo("value1");
    assertThat(cc.records.get(2).comments).isEqualTo(asList("Comment 1 to param 1", "Comment 2 to param 1"));

    assertThat(cc.records.get(3).trimmedKey()).isEqualTo("param2");
    assertThat(cc.records.get(3).trimmedValue()).isEqualTo("value2");
    assertThat(cc.records.get(3).comments)
      .isEqualTo(asList("Comment 1 to param 2", "Comment 2 to param 2", "Comment 3 to param 2"));

    assertThat(cc.records.get(4).trimmedKey()).isNull();
    assertThat(cc.records.get(4).trimmedValue()).isNull();
    assertThat(cc.records.get(4).comments).isEqualTo(asList("Left comment 3", "Left comment 4"));

    assertThat(cc.records.get(5).trimmedKey()).isEqualTo("param3");
    assertThat(cc.records.get(5).trimmedValue()).isEqualTo("value3");
    assertThat(cc.records.get(5).comments).isEqualTo(asList("Comment 1 to param 3", "Comment 2 to param 3"));

    assertThat(cc.records).hasSize(6);
  }

  @Test
  public void deserialize_noTrimming() {

    String text = ""
                    + "# Comment 2 to param 1\n"
                    + "param1   = value1\n"
                    + "\n"
                    + "# Comment 3 to param 2\n"
                    + "param2  = value2\n"
                    + "\n"
                    + "# Left comment 4\n"
                    + "\n"
                    + "# Comment 2 to param 3\n"
                    + "param3  =  value3\n"
                    + "\n";

    //
    //
    ConfContent cc = serializer.deserialize(text);
    //
    //

    assertThat(cc.records).isNotNull();

    assertThat(cc.records.get(0).key()).isEqualTo("param1   ");
    assertThat(cc.records.get(0).value()).isEqualTo(" value1");

    assertThat(cc.records.get(1).key()).isEqualTo("param2  ");
    assertThat(cc.records.get(1).value()).isEqualTo(" value2");

    assertThat(cc.records.get(2).key()).isNull();
    assertThat(cc.records.get(2).value()).isNull();

    assertThat(cc.records.get(3).key()).isEqualTo("param3  ");
    assertThat(cc.records.get(3).value()).isEqualTo("  value3");

    assertThat(cc.records).hasSize(4);
  }

  @Test
  public void serialize() {

    String expectedText = "" +
                            "# Common Header1\n" +
                            "# Common Header2\n" +
                            "#\n" +
                            "#   Common Header3\n" +
                            "# Common Header4\n" +
                            "\n" +
                            "# Left comment 1\n" +
                            "# Left comment 2\n" +
                            "\n" +
                            "# Comment 1 to param 1\n" +
                            "# Comment 2 to param 1\n" +
                            "param1=value1\n" +
                            "\n" +
                            "# Comment 1 to param 2\n" +
                            "# Comment 2 to param 2\n" +
                            "# Comment 3 to param 2\n" +
                            "param2=value2\n" +
                            "\n" +
                            "# Left comment 3\n" +
                            "# Left comment 4\n" +
                            "\n" +
                            "# Comment 1 to param 3\n" +
                            "# Comment 2 to param 3\n" +
                            "param3=value3\n" +
                            "\n" +
                            "# Comment 1 to param 4\n" +
                            "# Comment 2 to param 4\n" +
                            "param4";

    List<ConfRecord> records = new ArrayList<>();
    records.add(ConfRecord.ofComment("Common Header1\nCommon Header2\n\n  Common Header3\nCommon Header4"));
    records.add(ConfRecord.ofComment("Left comment 1\nLeft comment 2"));
    records.add(ConfRecord.of("param1", "value1", "Comment 1 to param 1\nComment 2 to param 1"));
    records.add(ConfRecord.of("param2", "value2", "Comment 1 to param 2\nComment 2 to param 2\nComment 3 to param 2"));
    records.add(ConfRecord.ofComment("Left comment 3\nLeft comment 4"));
    records.add(ConfRecord.of("param3", "value3", "Comment 1 to param 3\nComment 2 to param 3"));
    records.add(ConfRecord.of("param4", null, "Comment 1 to param 4\nComment 2 to param 4"));

    //
    //
    String text = serializer.serialize(ConfContent.of(records));
    //
    //

    assertThat(text).isEqualTo(expectedText);
  }
}
