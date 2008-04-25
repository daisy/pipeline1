package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum DISPIDSPTSI implements ComEnum {
    DISPIDSPTSI_ActiveOffset(1),
    DISPIDSPTSI_ActiveLength(2),
    DISPIDSPTSI_SelectionOffset(3),
    DISPIDSPTSI_SelectionLength(4),
    ;

    private final int value;
    DISPIDSPTSI(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
