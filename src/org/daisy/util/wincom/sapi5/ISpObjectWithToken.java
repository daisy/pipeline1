package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpObjectWithToken Interface
 */
@IID("{5B559F40-E952-11D2-BB91-00C04F8EE6C0}")
public interface ISpObjectWithToken extends Com4jObject {
    @VTID(3)
    void setObjectToken(
        org.daisy.util.wincom.sapi5.ISpObjectToken pToken);

    @VTID(4)
    void getObjectToken(
        Holder<org.daisy.util.wincom.sapi5.ISpObjectToken> ppToken);

}
