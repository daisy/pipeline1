package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechGrammarRuleStateTransition implements ComEnum {
    DISPID_SGRSTType(1),
    DISPID_SGRSTText(2),
    DISPID_SGRSTRule(3),
    DISPID_SGRSTWeight(4),
    DISPID_SGRSTPropertyName(5),
    DISPID_SGRSTPropertyId(6),
    DISPID_SGRSTPropertyValue(7),
    DISPID_SGRSTNextState(8),
    ;

    private final int value;
    DISPID_SpeechGrammarRuleStateTransition(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
