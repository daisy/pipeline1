package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseBuilder implements ComEnum {
    DISPID_SPPBRestorePhraseFromMemory(1),
    ;

    private final int value;
    DISPID_SpeechPhraseBuilder(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
