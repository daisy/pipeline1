package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SpeechPartOfSpeech implements ComEnum {
    SPSNotOverriden(-1),
    SPSUnknown(0),
    SPSNoun(4096),
    SPSVerb(8192),
    SPSModifier(12288),
    SPSFunction(16384),
    SPSInterjection(20480),
    ;

    private final int value;
    SpeechPartOfSpeech(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
