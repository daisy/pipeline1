package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechWaveFormatEx Interface
 */
@IID("{7A1EF0D5-1581-4741-88E4-209A49F11A10}")
public interface ISpeechWaveFormatEx extends Com4jObject {
    /**
     * FormatTag
     */
    @VTID(7)
    short formatTag();

    /**
     * FormatTag
     */
    @VTID(8)
    void formatTag(
        short formatTag);

    /**
     * Channels
     */
    @VTID(9)
    short channels();

    /**
     * Channels
     */
    @VTID(10)
    void channels(
        short channels);

    /**
     * SamplesPerSec
     */
    @VTID(11)
    int samplesPerSec();

    /**
     * SamplesPerSec
     */
    @VTID(12)
    void samplesPerSec(
        int samplesPerSec);

    /**
     * AvgBytesPerSec
     */
    @VTID(13)
    int avgBytesPerSec();

    /**
     * AvgBytesPerSec
     */
    @VTID(14)
    void avgBytesPerSec(
        int avgBytesPerSec);

    /**
     * BlockAlign
     */
    @VTID(15)
    short blockAlign();

    /**
     * BlockAlign
     */
    @VTID(16)
    void blockAlign(
        short blockAlign);

    /**
     * BitsPerSample
     */
    @VTID(17)
    short bitsPerSample();

    /**
     * BitsPerSample
     */
    @VTID(18)
    void bitsPerSample(
        short bitsPerSample);

    /**
     * ExtraData
     */
    @VTID(19)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object extraData();

    /**
     * ExtraData
     */
    @VTID(20)
    void extraData(
        @MarshalAs(NativeType.VARIANT) java.lang.Object extraData);

}
