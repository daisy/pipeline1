package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechTokenContext implements ComEnum {
    STCInprocServer(1),
    STCInprocHandler(2),
    STCLocalServer(4),
    STCRemoteServer(16),
    STCAll(23),
    ;

    private final int value;
    SpeechTokenContext(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
