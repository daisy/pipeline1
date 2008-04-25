package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpEventSource Interface
 */
@IID("{BE7A9CCE-5F9E-11D2-960F-00C04F8EE628}")
public interface ISpEventSource extends org.daisy.util.wincom.sapi5.ISpNotifySource {
    @VTID(10)
    void setInterest(
        long ullEventInterest,
        long ullQueuedInterest);

    }
