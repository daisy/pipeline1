package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechLexiconProns implements ComEnum {
    DISPID_SLPsCount(1),
    DISPID_SLPsItem(0),
    DISPID_SLPs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechLexiconProns(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
