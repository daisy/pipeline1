package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseRule implements ComEnum {
    DISPID_SPRuleName(1),
    DISPID_SPRuleId(2),
    DISPID_SPRuleFirstElement(3),
    DISPID_SPRuleNumberOfElements(4),
    DISPID_SPRuleParent(5),
    DISPID_SPRuleChildren(6),
    DISPID_SPRuleConfidence(7),
    DISPID_SPRuleEngineConfidence(8),
    ;

    private final int value;
    DISPID_SpeechPhraseRule(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
