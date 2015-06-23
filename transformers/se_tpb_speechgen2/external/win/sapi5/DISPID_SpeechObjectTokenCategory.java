package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechObjectTokenCategory implements ComEnum {
    DISPID_SOTCId(1),
    DISPID_SOTCDefault(2),
    DISPID_SOTCSetId(3),
    DISPID_SOTCGetDataKey(4),
    DISPID_SOTCEnumerateTokens(5),
    ;

    private final int value;
    DISPID_SpeechObjectTokenCategory(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
