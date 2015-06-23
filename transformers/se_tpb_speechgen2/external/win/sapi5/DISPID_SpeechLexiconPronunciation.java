package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechLexiconPronunciation implements ComEnum {
    DISPID_SLPType(1),
    DISPID_SLPLangId(2),
    DISPID_SLPPartOfSpeech(3),
    DISPID_SLPPhoneIds(4),
    DISPID_SLPSymbolic(5),
    ;

    private final int value;
    DISPID_SpeechLexiconPronunciation(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
