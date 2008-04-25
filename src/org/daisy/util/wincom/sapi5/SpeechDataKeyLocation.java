package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechDataKeyLocation implements ComEnum {
    SDKLDefaultLocation(0),
    SDKLCurrentUser(1),
    SDKLLocalMachine(2),
    SDKLCurrentConfig(5),
    ;

    private final int value;
    SpeechDataKeyLocation(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
