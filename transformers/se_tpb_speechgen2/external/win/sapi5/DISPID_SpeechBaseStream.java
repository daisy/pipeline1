package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechBaseStream implements ComEnum {
    DISPID_SBSFormat(1),
    DISPID_SBSRead(2),
    DISPID_SBSWrite(3),
    DISPID_SBSSeek(4),
    ;

    private final int value;
    DISPID_SpeechBaseStream(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
