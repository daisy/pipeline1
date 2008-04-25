package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechAudioStatus Interface
 */
@IID("{C62D9C91-7458-47F6-862D-1EF86FB0B278}")
public interface ISpeechAudioStatus extends Com4jObject {
    /**
     * FreeBufferSpace
     */
    @VTID(7)
    int freeBufferSpace();

    /**
     * NonBlockingIO
     */
    @VTID(8)
    int nonBlockingIO();

    /**
     * State
     */
    @VTID(9)
    org.daisy.util.wincom.sapi5.SpeechAudioState state();

    /**
     * CurrentSeekPosition
     */
    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object currentSeekPosition();

    /**
     * CurrentDevicePosition
     */
    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object currentDevicePosition();

}
