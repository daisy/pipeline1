package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechTokenShellFolder implements ComEnum {
    STSF_AppData(26),
    STSF_LocalAppData(28),
    STSF_CommonAppData(35),
    STSF_FlagCreate(32768),
    ;

    private final int value;
    SpeechTokenShellFolder(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
