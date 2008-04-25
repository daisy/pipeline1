package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechWordType implements ComEnum {
    SWTAdded(1),
    SWTDeleted(2),
    ;

    private final int value;
    SpeechWordType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
