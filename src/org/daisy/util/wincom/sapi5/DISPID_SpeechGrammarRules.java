package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechGrammarRules implements ComEnum {
    DISPID_SGRsCount(1),
    DISPID_SGRsDynamic(2),
    DISPID_SGRsAdd(3),
    DISPID_SGRsCommit(4),
    DISPID_SGRsCommitAndSave(5),
    DISPID_SGRsFindRule(6),
    DISPID_SGRsItem(0),
    DISPID_SGRs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechGrammarRules(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
