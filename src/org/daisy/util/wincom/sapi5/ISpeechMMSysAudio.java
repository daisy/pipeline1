package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechMMSysAudio Interface
 */
@IID("{3C76AF6D-1FD7-4831-81D1-3B71D5A13C44}")
public interface ISpeechMMSysAudio extends org.daisy.util.wincom.sapi5.ISpeechAudio {
    /**
     * DeviceId
     */
    @VTID(21)
    int deviceId();

    /**
     * DeviceId
     */
    @VTID(22)
    void deviceId(
        int deviceId);

    /**
     * LineId
     */
    @VTID(23)
    int lineId();

    /**
     * LineId
     */
    @VTID(24)
    void lineId(
        int lineId);

    /**
     * MMHandle
     */
    @VTID(25)
    int mmHandle();

}
