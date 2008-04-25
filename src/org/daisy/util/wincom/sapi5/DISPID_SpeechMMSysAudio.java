package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechMMSysAudio implements ComEnum {
    DISPID_SMSADeviceId(300),
    DISPID_SMSALineId(301),
    DISPID_SMSAMMHandle(302),
    ;

    private final int value;
    DISPID_SpeechMMSysAudio(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
