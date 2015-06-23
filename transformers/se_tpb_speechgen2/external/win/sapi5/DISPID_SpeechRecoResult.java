package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechRecoResult implements ComEnum {
    DISPID_SRRRecoContext(1),
    DISPID_SRRTimes(2),
    DISPID_SRRAudioFormat(3),
    DISPID_SRRPhraseInfo(4),
    DISPID_SRRAlternates(5),
    DISPID_SRRAudio(6),
    DISPID_SRRSpeakAudio(7),
    DISPID_SRRSaveToMemory(8),
    DISPID_SRRDiscardResultInfo(9),
    ;

    private final int value;
    DISPID_SpeechRecoResult(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
