package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechRecognizerStatus implements ComEnum {
    DISPID_SRSAudioStatus(1),
    DISPID_SRSCurrentStreamPosition(2),
    DISPID_SRSCurrentStreamNumber(3),
    DISPID_SRSNumberOfActiveRules(4),
    DISPID_SRSClsidEngine(5),
    DISPID_SRSSupportedLanguages(6),
    ;

    private final int value;
    DISPID_SpeechRecognizerStatus(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
