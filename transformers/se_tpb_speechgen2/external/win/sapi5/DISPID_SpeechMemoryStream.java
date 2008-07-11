package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechMemoryStream implements ComEnum {
    DISPID_SMSSetData(100),
    DISPID_SMSGetData(101),
    ;

    private final int value;
    DISPID_SpeechMemoryStream(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
