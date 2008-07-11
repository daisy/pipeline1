package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SPPARTOFSPEECH implements ComEnum {
    SPPS_NotOverriden(-1),
    SPPS_Unknown(0),
    SPPS_Noun(4096),
    SPPS_Verb(8192),
    SPPS_Modifier(12288),
    SPPS_Function(16384),
    SPPS_Interjection(20480),
    ;

    private final int value;
    SPPARTOFSPEECH(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
