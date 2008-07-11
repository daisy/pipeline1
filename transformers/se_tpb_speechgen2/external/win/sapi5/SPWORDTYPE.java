package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SPWORDTYPE implements ComEnum {
    eWORDTYPE_ADDED(1),
    eWORDTYPE_DELETED(2),
    ;

    private final int value;
    SPWORDTYPE(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
