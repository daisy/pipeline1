package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SpeechEngineConfidence implements ComEnum {
    SECLowConfidence(-1),
    SECNormalConfidence(0),
    SECHighConfidence(1),
    ;

    private final int value;
    SpeechEngineConfidence(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
