package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechRuleState implements ComEnum {
    SGDSInactive(0),
    SGDSActive(1),
    SGDSActiveWithAutoPause(3),
    ;

    private final int value;
    SpeechRuleState(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
