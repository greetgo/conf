package kz.greetgo.conf.core;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfAccessStdSerializerTest {

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
    List<ConfRecord> confRecordList = ConfAccessStdSerializer.deserialize(text);
    //
    //

    assertThat(confRecordList).isNotNull();

    assertThat(confRecordList.get(0).key).isNull();
    assertThat(confRecordList.get(0).value).isNull();
    assertThat(confRecordList.get(0).hasValue).isFalse();
    assertThat(confRecordList.get(0).comment).isEqualTo("Common Header1\nCommon Header2\n\n  Common Header3\nCommon Header4");

    assertThat(confRecordList.get(1).key).isNull();
    assertThat(confRecordList.get(1).value).isNull();
    assertThat(confRecordList.get(1).hasValue).isFalse();
    assertThat(confRecordList.get(1).comment).isEqualTo("Left comment 1\nLeft comment 2");

    assertThat(confRecordList.get(2).key).isEqualTo("param1");
    assertThat(confRecordList.get(2).value).isEqualTo("value1");
    assertThat(confRecordList.get(2).hasValue).isTrue();
    assertThat(confRecordList.get(2).comment).isEqualTo("Comment 1 to param 1\nComment 1 to param 2");

    assertThat(confRecordList.get(3).key).isEqualTo("param2");
    assertThat(confRecordList.get(3).value).isEqualTo("value2");
    assertThat(confRecordList.get(3).hasValue).isTrue();
    assertThat(confRecordList.get(3).comment).isEqualTo("Comment 1 to param 2\nComment 2 to param 2\nComment 3 to param 2");

    assertThat(confRecordList.get(4).key).isNull();
    assertThat(confRecordList.get(4).value).isNull();
    assertThat(confRecordList.get(4).hasValue).isFalse();
    assertThat(confRecordList.get(4).comment).isEqualTo("Left comment 3\nLeft comment 4");

    assertThat(confRecordList.get(5).key).isEqualTo("param3");
    assertThat(confRecordList.get(5).value).isEqualTo("value3");
    assertThat(confRecordList.get(5).hasValue).isTrue();
    assertThat(confRecordList.get(5).comment).isEqualTo("Comment 1 to param 3\nComment 2 to param 3");

    assertThat(confRecordList).hasSize(6);
  }

  @Test
  public void serialize() {

    String expectedText = ""
      + "# Common Header1\n"
      + "# Common Header2\n"
      + "#\n"
      + "#   Common Header3\n"
      + "# Common Header4\n"
      + "\n"
      + "# Left comment 1\n"
      + "# Left comment 2\n"
      + "\n"
      + "# Comment 1 to param 1\n"
      + "# Comment 2 to param 1\n"
      + "param1=value1\n"
      + "\n"
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
      + "# Comment 1 to param 4\n"
      + "# Comment 2 to param 4\n"
      + "param4\n";

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
    String text = ConfAccessStdSerializer.serialize(records);
    //
    //

    assertThat(text).isEqualTo(expectedText);
  }
}
