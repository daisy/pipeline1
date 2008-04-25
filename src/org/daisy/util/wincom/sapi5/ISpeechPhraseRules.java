package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseRules Interface
 */
@IID("{9047D593-01DD-4B72-81A3-E4A0CA69F407}")
public interface ISpeechPhraseRules extends Com4jObject,Iterable<Com4jObject> {
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
    org.daisy.util.wincom.sapi5.ISpeechPhraseRule item(
        int index);

    /**
     * Enumerates the Rules
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
