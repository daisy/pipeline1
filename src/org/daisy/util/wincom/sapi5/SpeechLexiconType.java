package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechLexiconType implements ComEnum {
    SLTUser(1),
    SLTApp(2),
    ;

    private final int value;
    SpeechLexiconType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
