package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseReplacement implements ComEnum {
    DISPID_SPRDisplayAttributes(1),
    DISPID_SPRText(2),
    DISPID_SPRFirstElement(3),
    DISPID_SPRNumberOfElements(4),
    ;

    private final int value;
    DISPID_SpeechPhraseReplacement(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
