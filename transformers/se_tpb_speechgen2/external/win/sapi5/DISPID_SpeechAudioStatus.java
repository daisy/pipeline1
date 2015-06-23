package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechAudioStatus implements ComEnum {
    DISPID_SASFreeBufferSpace(1),
    DISPID_SASNonBlockingIO(2),
    DISPID_SASState(3),
    DISPID_SASCurrentSeekPosition(4),
    DISPID_SASCurrentDevicePosition(5),
    ;

    private final int value;
    DISPID_SpeechAudioStatus(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
