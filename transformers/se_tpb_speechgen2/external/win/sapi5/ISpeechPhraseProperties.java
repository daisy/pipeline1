package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseProperties Interface
 */
@IID("{08166B47-102E-4B23-A599-BDB98DBFD1F4}")
public interface ISpeechPhraseProperties extends Com4jObject,Iterable<Com4jObject> {
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
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperty item(
        int index);

    /**
     * Enumerates the alternates
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
