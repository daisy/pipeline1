package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhoneConverter implements ComEnum {
    DISPID_SPCLangId(1),
    DISPID_SPCPhoneToId(2),
    DISPID_SPCIdToPhone(3),
    ;

    private final int value;
    DISPID_SpeechPhoneConverter(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
