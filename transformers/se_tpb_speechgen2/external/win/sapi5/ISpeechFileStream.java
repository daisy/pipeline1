package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechFileStream Interface
 */
@IID("{AF67F125-AB39-4E93-B4A2-CC2E66E182A7}")
public interface ISpeechFileStream extends se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream {
    /**
     * Open
     */
    @VTID(12)
    void open(
        java.lang.String fileName,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechStreamFileMode fileMode,
        @DefaultValue("0")boolean doEvents);

    /**
     * Close
     */
    @VTID(13)
    void close();

}
