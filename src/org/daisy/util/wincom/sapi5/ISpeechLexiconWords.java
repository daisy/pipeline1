package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechLexiconWords Interface
 */
@IID("{8D199862-415E-47D5-AC4F-FAA608B424E6}")
public interface ISpeechLexiconWords extends Com4jObject,Iterable<Com4jObject> {
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
    org.daisy.util.wincom.sapi5.ISpeechLexiconWord item(
        int index);

    /**
     * Enumerates the tokens
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
