package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechFileStream implements ComEnum {
    DISPID_SFSOpen(100),
    DISPID_SFSClose(101),
    ;

    private final int value;
    DISPID_SpeechFileStream(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
