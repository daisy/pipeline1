package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechAudioFormat implements ComEnum {
    DISPID_SAFType(1),
    DISPID_SAFGuid(2),
    DISPID_SAFGetWaveFormatEx(3),
    DISPID_SAFSetWaveFormatEx(4),
    ;

    private final int value;
    DISPID_SpeechAudioFormat(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
