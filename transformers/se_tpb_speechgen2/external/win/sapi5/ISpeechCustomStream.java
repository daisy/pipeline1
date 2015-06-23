package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechCustomStream Interface
 */
@IID("{1A9E9F4F-104F-4DB8-A115-EFD7FD0C97AE}")
public interface ISpeechCustomStream extends se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream {
    /**
     * BaseStream
     */
    @VTID(12)
    com4j.Com4jObject baseStream();

    /**
     * BaseStream
     */
    @VTID(13)
    void baseStream(
        com4j.Com4jObject ppUnkStream);

}
