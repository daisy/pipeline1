package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpEventSource Interface
 */
@IID("{BE7A9CCE-5F9E-11D2-960F-00C04F8EE628}")
public interface ISpEventSource extends se_tpb_speechgen2.external.win.sapi5.ISpNotifySource {
    @VTID(10)
    void setInterest(
        long ullEventInterest,
        long ullQueuedInterest);

    }
