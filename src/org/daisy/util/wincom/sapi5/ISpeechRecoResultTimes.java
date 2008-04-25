package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechRecoResultTimes Interface
 */
@IID("{62B3B8FB-F6E7-41BE-BDCB-056B1C29EFC0}")
public interface ISpeechRecoResultTimes extends Com4jObject {
    /**
     * StreamTime
     */
    @VTID(7)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object streamTime();

    /**
     * Length
     */
    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object length();

    /**
     * TickCount
     */
    @VTID(9)
    int tickCount();

    /**
     * Start
     */
    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object offsetFromStart();

}
