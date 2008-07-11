package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechAudio Interface
 */
@IID("{CFF8E175-019E-11D3-A08E-00C04F8EF9B5}")
public interface ISpeechAudio extends se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream {
    /**
     * Status
     */
    @VTID(12)
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioStatus status();

    /**
     * BufferInfo
     */
    @VTID(13)
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioBufferInfo bufferInfo();

    /**
     * DefaultFormat
     */
    @VTID(14)
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat defaultFormat();

    /**
     * Volume
     */
    @VTID(15)
    int volume();

    /**
     * Volume
     */
    @VTID(16)
    void volume(
        int volume);

    /**
     * BufferNotifySize
     */
    @VTID(17)
    int bufferNotifySize();

    /**
     * BufferNotifySize
     */
    @VTID(18)
    void bufferNotifySize(
        int bufferNotifySize);

    /**
     * EventHandle
     */
    @VTID(19)
    int eventHandle();

    /**
     * SetState
     */
    @VTID(20)
    void setState(
        se_tpb_speechgen2.external.win.sapi5.SpeechAudioState state);

}
