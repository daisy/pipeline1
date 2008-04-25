package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechGrammarState implements ComEnum {
    SGSEnabled(1),
    SGSDisabled(0),
    SGSExclusive(3),
    ;

    private final int value;
    SpeechGrammarState(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
