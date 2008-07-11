package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechGrammarRuleStateTransitions implements ComEnum {
    DISPID_SGRSTsCount(1),
    DISPID_SGRSTsItem(0),
    DISPID_SGRSTs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechGrammarRuleStateTransitions(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
