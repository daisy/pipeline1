package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechObjectTokens implements ComEnum {
    DISPID_SOTsCount(1),
    DISPID_SOTsItem(0),
    DISPID_SOTs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechObjectTokens(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
