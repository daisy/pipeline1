package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SpeechVoiceSpeakFlags implements ComEnum {
    SVSFDefault(0),
    SVSFlagsAsync(1),
    SVSFPurgeBeforeSpeak(2),
    SVSFIsFilename(4),
    SVSFIsXML(8),
    SVSFIsNotXML(16),
    SVSFPersistXML(32),
    SVSFNLPSpeakPunc(64),
    SVSFNLPMask(64),
    SVSFVoiceMask(127),
    SVSFUnusedFlags(-128),
    ;

    private final int value;
    SpeechVoiceSpeakFlags(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
