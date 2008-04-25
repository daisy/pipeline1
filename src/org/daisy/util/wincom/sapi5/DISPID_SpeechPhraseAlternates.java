package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseAlternates implements ComEnum {
    DISPID_SPAsCount(1),
    DISPID_SPAsItem(0),
    DISPID_SPAs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechPhraseAlternates(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
