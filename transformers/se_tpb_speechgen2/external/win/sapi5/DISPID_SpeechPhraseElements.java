package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseElements implements ComEnum {
    DISPID_SPEsCount(1),
    DISPID_SPEsItem(0),
    DISPID_SPEs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechPhraseElements(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
