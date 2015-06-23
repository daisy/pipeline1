package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SPGRAMMARSTATE implements ComEnum {
    SPGS_DISABLED(0),
    SPGS_ENABLED(1),
    SPGS_EXCLUSIVE(3),
    ;

    private final int value;
    SPGRAMMARSTATE(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
