package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpNotifySink Interface
 */
@IID("{259684DC-37C3-11D2-9603-00C04F8EE628}")
public interface ISpNotifySink extends Com4jObject {
    @VTID(3)
    void notify_();

}
