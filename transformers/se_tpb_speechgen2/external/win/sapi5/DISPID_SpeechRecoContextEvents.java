package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechRecoContextEvents implements ComEnum {
    DISPID_SRCEStartStream(1),
    DISPID_SRCEEndStream(2),
    DISPID_SRCEBookmark(3),
    DISPID_SRCESoundStart(4),
    DISPID_SRCESoundEnd(5),
    DISPID_SRCEPhraseStart(6),
    DISPID_SRCERecognition(7),
    DISPID_SRCEHypothesis(8),
    DISPID_SRCEPropertyNumberChange(9),
    DISPID_SRCEPropertyStringChange(10),
    DISPID_SRCEFalseRecognition(11),
    DISPID_SRCEInterference(12),
    DISPID_SRCERequestUI(13),
    DISPID_SRCERecognizerStateChange(14),
    DISPID_SRCEAdaptation(15),
    DISPID_SRCERecognitionForOtherContext(16),
    DISPID_SRCEAudioLevel(17),
    DISPID_SRCEEnginePrivate(18),
    ;

    private final int value;
    DISPID_SpeechRecoContextEvents(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
