package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechVoiceEvent implements ComEnum {
    DISPID_SVEStreamStart(1),
    DISPID_SVEStreamEnd(2),
    DISPID_SVEVoiceChange(3),
    DISPID_SVEBookmark(4),
    DISPID_SVEWord(5),
    DISPID_SVEPhoneme(6),
    DISPID_SVESentenceBoundary(7),
    DISPID_SVEViseme(8),
    DISPID_SVEAudioLevel(9),
    DISPID_SVEEnginePrivate(10),
    ;

    private final int value;
    DISPID_SpeechVoiceEvent(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
