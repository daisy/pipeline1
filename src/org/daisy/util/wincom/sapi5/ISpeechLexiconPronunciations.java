package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechLexiconPronunciations Interface
 */
@IID("{72829128-5682-4704-A0D4-3E2BB6F2EAD3}")
public interface ISpeechLexiconPronunciations extends Com4jObject,Iterable<Com4jObject> {
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
    org.daisy.util.wincom.sapi5.ISpeechLexiconPronunciation item(
        int index);

    /**
     * Enumerates the tokens
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
