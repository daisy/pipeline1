package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpMMSysAudio Interface
 */
@IID("{15806F6E-1D70-4B48-98E6-3B1A007509AB}")
public interface ISpMMSysAudio extends se_tpb_speechgen2.external.win.sapi5.ISpAudio {
    @VTID(26)
    int getDeviceId();

    @VTID(27)
    void setDeviceId(
        int uDeviceId);

    @VTID(28)
    void getMMHandle(
        Holder<java.nio.Buffer> pHandle);

    @VTID(29)
    int getLineId();

    @VTID(30)
    void setLineId(
        int uLineId);

}
