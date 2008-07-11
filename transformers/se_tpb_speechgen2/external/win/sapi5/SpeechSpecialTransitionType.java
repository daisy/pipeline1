package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SpeechSpecialTransitionType implements ComEnum {
    SSTTWildcard(1),
    SSTTDictation(2),
    SSTTTextBuffer(3),
    ;

    private final int value;
    SpeechSpecialTransitionType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
