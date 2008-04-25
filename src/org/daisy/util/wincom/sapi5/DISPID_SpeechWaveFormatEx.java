package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechWaveFormatEx implements ComEnum {
    DISPID_SWFEFormatTag(1),
    DISPID_SWFEChannels(2),
    DISPID_SWFESamplesPerSec(3),
    DISPID_SWFEAvgBytesPerSec(4),
    DISPID_SWFEBlockAlign(5),
    DISPID_SWFEBitsPerSample(6),
    DISPID_SWFEExtraData(7),
    ;

    private final int value;
    DISPID_SpeechWaveFormatEx(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
