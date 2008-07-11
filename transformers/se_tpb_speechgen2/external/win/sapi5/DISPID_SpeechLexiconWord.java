package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechLexiconWord implements ComEnum {
    DISPID_SLWLangId(1),
    DISPID_SLWType(2),
    DISPID_SLWWord(3),
    DISPID_SLWPronunciations(4),
    ;

    private final int value;
    DISPID_SpeechLexiconWord(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
