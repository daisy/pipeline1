package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseReplacements implements ComEnum {
    DISPID_SPRsCount(1),
    DISPID_SPRsItem(0),
    DISPID_SPRs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechPhraseReplacements(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
