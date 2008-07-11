package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechLexiconWords implements ComEnum {
    DISPID_SLWsCount(1),
    DISPID_SLWsItem(0),
    DISPID_SLWs_NewEnum(-4),
    ;

    private final int value;
    DISPID_SpeechLexiconWords(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
