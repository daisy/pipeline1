package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechAudioBufferInfo Interface
 */
@IID("{11B103D8-1142-4EDF-A093-82FB3915F8CC}")
public interface ISpeechAudioBufferInfo extends Com4jObject {
    /**
     * MinNotification
     */
    @VTID(7)
    int minNotification();

    /**
     * MinNotification
     */
    @VTID(8)
    void minNotification(
        int minNotification);

    /**
     * BufferSize
     */
    @VTID(9)
    int bufferSize();

    /**
     * BufferSize
     */
    @VTID(10)
    void bufferSize(
        int bufferSize);

    /**
     * EventBias
     */
    @VTID(11)
    int eventBias();

    /**
     * EventBias
     */
    @VTID(12)
    void eventBias(
        int eventBias);

}
