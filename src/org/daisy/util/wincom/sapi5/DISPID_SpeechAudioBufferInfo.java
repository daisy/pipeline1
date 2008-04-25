package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechAudioBufferInfo implements ComEnum {
    DISPID_SABIMinNotification(1),
    DISPID_SABIBufferSize(2),
    DISPID_SABIEventBias(3),
    ;

    private final int value;
    DISPID_SpeechAudioBufferInfo(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
