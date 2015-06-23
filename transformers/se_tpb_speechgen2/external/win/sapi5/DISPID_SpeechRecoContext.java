package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechRecoContext implements ComEnum {
    DISPID_SRCRecognizer(1),
    DISPID_SRCAudioInInterferenceStatus(2),
    DISPID_SRCRequestedUIType(3),
    DISPID_SRCVoice(4),
    DISPID_SRAllowVoiceFormatMatchingOnNextSet(5),
    DISPID_SRCVoicePurgeEvent(6),
    DISPID_SRCEventInterests(7),
    DISPID_SRCCmdMaxAlternates(8),
    DISPID_SRCState(9),
    DISPID_SRCRetainedAudio(10),
    DISPID_SRCRetainedAudioFormat(11),
    DISPID_SRCPause(12),
    DISPID_SRCResume(13),
    DISPID_SRCCreateGrammar(14),
    DISPID_SRCCreateResultFromMemory(15),
    DISPID_SRCBookmark(16),
    DISPID_SRCSetAdaptationData(17),
    ;

    private final int value;
    DISPID_SpeechRecoContext(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
