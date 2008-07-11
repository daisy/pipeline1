package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SpeechRunState implements ComEnum {
    SRSEDone(1),
    SRSEIsSpeaking(2),
    ;

    private final int value;
    SpeechRunState(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
