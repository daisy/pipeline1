package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseProperties implements ComEnum {
    DISPID_SPPsCount(1),
    DISPID_SPPsItem(0),
    DISPID_SPPs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechPhraseProperties(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
