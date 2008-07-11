package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechGrammarRule implements ComEnum {
    DISPID_SGRAttributes(1),
    DISPID_SGRInitialState(2),
    DISPID_SGRName(3),
    DISPID_SGRId(4),
    DISPID_SGRClear(5),
    DISPID_SGRAddResource(6),
    DISPID_SGRAddState(7),
    ;

    private final int value;
    DISPID_SpeechGrammarRule(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
