package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseReplacements Interface
 */
@IID("{38BC662F-2257-4525-959E-2069D2596C05}")
public interface ISpeechPhraseReplacements extends Com4jObject,Iterable<Com4jObject> {
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
    org.daisy.util.wincom.sapi5.ISpeechPhraseReplacement item(
        int index);

    /**
     * Enumerates the tokens
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
