package se_tpb_speechgen2.external.win.sapi5  ;

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
    se_tpb_speechgen2.external.win.sapi5.SpeechAudioFormatType type();

    /**
     * Type
     */
    @VTID(8)
    void type(
        se_tpb_speechgen2.external.win.sapi5.SpeechAudioFormatType audioFormat);

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
    se_tpb_speechgen2.external.win.sapi5.ISpeechWaveFormatEx getWaveFormatEx();

    /**
     * SetWaveFormatEx
     */
    @VTID(12)
    void setWaveFormatEx(
        se_tpb_speechgen2.external.win.sapi5.ISpeechWaveFormatEx waveFormatEx);

}
