package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechLexicon implements ComEnum {
    DISPID_SLGenerationId(1),
    DISPID_SLGetWords(2),
    DISPID_SLAddPronunciation(3),
    DISPID_SLAddPronunciationByPhoneIds(4),
    DISPID_SLRemovePronunciation(5),
    DISPID_SLRemovePronunciationByPhoneIds(6),
    DISPID_SLGetPronunciations(7),
    DISPID_SLGetGenerationChange(8),
    ;

    private final int value;
    DISPID_SpeechLexicon(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
