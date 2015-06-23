package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechRecoResultTimes implements ComEnum {
    DISPID_SRRTStreamTime(1),
    DISPID_SRRTLength(2),
    DISPID_SRRTTickCount(3),
    DISPID_SRRTOffsetFromStart(4),
    ;

    private final int value;
    DISPID_SpeechRecoResultTimes(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
