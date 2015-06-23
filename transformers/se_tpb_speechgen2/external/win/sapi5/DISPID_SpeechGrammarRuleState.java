package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechGrammarRuleState implements ComEnum {
    DISPID_SGRSRule(1),
    DISPID_SGRSTransitions(2),
    DISPID_SGRSAddWordTransition(3),
    DISPID_SGRSAddRuleTransition(4),
    DISPID_SGRSAddSpecialTransition(5),
    ;

    private final int value;
    DISPID_SpeechGrammarRuleState(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
