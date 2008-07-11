package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseRules implements ComEnum {
    DISPID_SPRulesCount(1),
    DISPID_SPRulesItem(0),
    DISPID_SPRules_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechPhraseRules(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
