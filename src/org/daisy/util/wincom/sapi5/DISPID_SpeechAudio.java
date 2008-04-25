package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechAudio implements ComEnum {
    DISPID_SAStatus(200),
    DISPID_SABufferInfo(201),
    DISPID_SADefaultFormat(202),
    DISPID_SAVolume(203),
    DISPID_SABufferNotifySize(204),
    DISPID_SAEventHandle(205),
    DISPID_SASetState(206),
    ;

    private final int value;
    DISPID_SpeechAudio(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
