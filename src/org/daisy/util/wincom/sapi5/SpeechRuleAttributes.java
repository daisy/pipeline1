package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechRuleAttributes implements ComEnum {
    SRATopLevel(1),
    SRADefaultToActive(2),
    SRAExport(4),
    SRAImport(8),
    SRAInterpreter(16),
    SRADynamic(32),
    ;

    private final int value;
    SpeechRuleAttributes(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
