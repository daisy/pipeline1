package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechFileStream Interface
 */
@IID("{AF67F125-AB39-4E93-B4A2-CC2E66E182A7}")
public interface ISpeechFileStream extends org.daisy.util.wincom.sapi5.ISpeechBaseStream {
    /**
     * Open
     */
    @VTID(12)
    void open(
        java.lang.String fileName,
        @DefaultValue("0")org.daisy.util.wincom.sapi5.SpeechStreamFileMode fileMode,
        @DefaultValue("0")boolean doEvents);

    /**
     * Close
     */
    @VTID(13)
    void close();

}
