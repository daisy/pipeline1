package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseInfo implements ComEnum {
    DISPID_SPILanguageId(1),
    DISPID_SPIGrammarId(2),
    DISPID_SPIStartTime(3),
    DISPID_SPIAudioStreamPosition(4),
    DISPID_SPIAudioSizeBytes(5),
    DISPID_SPIRetainedSizeBytes(6),
    DISPID_SPIAudioSizeTime(7),
    DISPID_SPIRule(8),
    DISPID_SPIProperties(9),
    DISPID_SPIElements(10),
    DISPID_SPIReplacements(11),
    DISPID_SPIEngineId(12),
    DISPID_SPIEnginePrivateData(13),
    DISPID_SPISaveToMemory(14),
    DISPID_SPIGetText(15),
    DISPID_SPIGetDisplayAttributes(16),
    ;

    private final int value;
    DISPID_SpeechPhraseInfo(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
