package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechBaseStream Interface
 */
@IID("{6450336F-7D49-4CED-8097-49D6DEE37294}")
public interface ISpeechBaseStream extends Com4jObject {
    /**
     * Format
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat format();

    /**
     * Format
     */
    @VTID(8)
    void format(
        se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat audioFormat);

    /**
     * Read
     */
    @VTID(9)
    int read(
        java.lang.Object buffer,
        int numberOfBytes);

    /**
     * Write
     */
    @VTID(10)
    int write(
        @MarshalAs(NativeType.VARIANT) java.lang.Object buffer);

    /**
     * Seek
     */
    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object seek(
        @MarshalAs(NativeType.VARIANT) java.lang.Object position,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechStreamSeekPositionType origin);

}
