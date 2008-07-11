package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechGrammarRuleStateTransitions Interface
 */
@IID("{EABCE657-75BC-44A2-AA7F-C56476742963}")
public interface ISpeechGrammarRuleStateTransitions extends Com4jObject,Iterable<Com4jObject> {
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
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleStateTransition item(
        int index);

    /**
     * Enumerates the transitions
     */
    @VTID(9)
    java.util.Iterator<Com4jObject> iterator();

}
