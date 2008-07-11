package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SPRULESTATE implements ComEnum {
    SPRS_INACTIVE(0),
    SPRS_ACTIVE(1),
    SPRS_ACTIVE_WITH_AUTO_PAUSE(3),
    ;

    private final int value;
    SPRULESTATE(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
