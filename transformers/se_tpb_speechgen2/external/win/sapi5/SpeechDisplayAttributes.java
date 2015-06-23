package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SpeechDisplayAttributes implements ComEnum {
    SDA_No_Trailing_Space(0),
    SDA_One_Trailing_Space(2),
    SDA_Two_Trailing_Spaces(4),
    SDA_Consume_Leading_Spaces(8),
    ;

    private final int value;
    SpeechDisplayAttributes(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
