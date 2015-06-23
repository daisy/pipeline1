package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseAlternates Interface
 */
@IID("{B238B6D5-F276-4C3D-A6C1-2974801C3CC2}")
public interface ISpeechPhraseAlternates extends Com4jObject,Iterable<Com4jObject> {
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
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseAlternate item(
        int index);

    /**
     * Enumerates the alternates
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
