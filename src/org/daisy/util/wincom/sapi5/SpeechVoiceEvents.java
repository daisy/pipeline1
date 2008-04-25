package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechVoiceEvents implements ComEnum {
    SVEStartInputStream(2),
    SVEEndInputStream(4),
    SVEVoiceChange(8),
    SVEBookmark(16),
    SVEWordBoundary(32),
    SVEPhoneme(64),
    SVESentenceBoundary(128),
    SVEViseme(256),
    SVEAudioLevel(512),
    SVEPrivate(32768),
    SVEAllEvents(33790),
    ;

    private final int value;
    SpeechVoiceEvents(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
