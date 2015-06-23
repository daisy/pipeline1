package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseElements Interface
 */
@IID("{0626B328-3478-467D-A0B3-D0853B93DDA3}")
public interface ISpeechPhraseElements extends Com4jObject,Iterable<Com4jObject> {
    /**
     * Count
     */
    @VTID(7)
    int count();

    /**
     * Item
     */
    @VTID(8)
    @DefaultMethod
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseElement item(
        int index);

    /**
     * Enumerates the tokens
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
