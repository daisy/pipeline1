package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SPDATAKEYLOCATION implements ComEnum {
    SPDKL_DefaultLocation(0),
    SPDKL_CurrentUser(1),
    SPDKL_LocalMachine(2),
    SPDKL_CurrentConfig(5),
    ;

    private final int value;
    SPDATAKEYLOCATION(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
