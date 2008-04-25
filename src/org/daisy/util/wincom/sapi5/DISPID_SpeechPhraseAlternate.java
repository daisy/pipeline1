package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseAlternate implements ComEnum {
    DISPID_SPARecoResult(1),
    DISPID_SPAStartElementInResult(2),
    DISPID_SPANumberOfElementsInResult(3),
    DISPID_SPAPhraseInfo(4),
    DISPID_SPACommit(5),
    ;

    private final int value;
    DISPID_SpeechPhraseAlternate(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
