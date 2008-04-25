package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPID_SpeechObjectToken implements ComEnum {
    DISPID_SOTId(1),
    DISPID_SOTDataKey(2),
    DISPID_SOTCategory(3),
    DISPID_SOTGetDescription(4),
    DISPID_SOTSetId(5),
    DISPID_SOTGetAttribute(6),
    DISPID_SOTCreateInstance(7),
    DISPID_SOTRemove(8),
    DISPID_SOTGetStorageFileName(9),
    DISPID_SOTRemoveStorageFileName(10),
    DISPID_SOTIsUISupported(11),
    DISPID_SOTDisplayUI(12),
    DISPID_SOTMatchesAttributes(13),
    ;

    private final int value;
    DISPID_SpeechObjectToken(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
