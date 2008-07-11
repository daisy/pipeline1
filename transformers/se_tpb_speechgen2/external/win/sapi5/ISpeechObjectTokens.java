package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechObjectTokens Interface
 */
@IID("{9285B776-2E7B-4BC0-B53E-580EB6FA967F}")
public interface ISpeechObjectTokens extends Com4jObject,Iterable<Com4jObject> {
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
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken item(
        int index);

    /**
     * Enumerates the tokens
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
