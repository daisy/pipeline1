package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechRecognizer implements ComEnum {
    DISPID_SRRecognizer(1),
    DISPID_SRAllowAudioInputFormatChangesOnNextSet(2),
    DISPID_SRAudioInput(3),
    DISPID_SRAudioInputStream(4),
    DISPID_SRIsShared(5),
    DISPID_SRState(6),
    DISPID_SRStatus(7),
    DISPID_SRProfile(8),
    DISPID_SREmulateRecognition(9),
    DISPID_SRCreateRecoContext(10),
    DISPID_SRGetFormat(11),
    DISPID_SRSetPropertyNumber(12),
    DISPID_SRGetPropertyNumber(13),
    DISPID_SRSetPropertyString(14),
    DISPID_SRGetPropertyString(15),
    DISPID_SRIsUISupported(16),
    DISPID_SRDisplayUI(17),
    DISPID_SRGetRecognizers(18),
    DISPID_SVGetAudioInputs(19),
    DISPID_SVGetProfiles(20),
    ;

    private final int value;
    DISPID_SpeechRecognizer(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
