package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechAudioFormat Interface
 */
@IID("{E6E9C590-3E18-40E3-8299-061F98BDE7C7}")
public interface ISpeechAudioFormat extends Com4jObject {
    /**
     * Type
     */
    @VTID(7)
    org.daisy.util.wincom.sapi5.SpeechAudioFormatType type();

    /**
     * Type
     */
    @VTID(8)
    void type(
        org.daisy.util.wincom.sapi5.SpeechAudioFormatType audioFormat);

    /**
     * Guid
     */
    @VTID(9)
    java.lang.String guid();

    /**
     * Guid
     */
    @VTID(10)
    void guid(
        java.lang.String guid);

    /**
     * GetWaveFormatEx
     */
    @VTID(11)
    org.daisy.util.wincom.sapi5.ISpeechWaveFormatEx getWaveFormatEx();

    /**
     * SetWaveFormatEx
     */
    @VTID(12)
    void setWaveFormatEx(
        org.daisy.util.wincom.sapi5.ISpeechWaveFormatEx waveFormatEx);

}
