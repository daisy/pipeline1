package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechGrammarRules Interface
 */
@IID("{6FFA3B44-FC2D-40D1-8AFC-32911C7F1AD1}")
public interface ISpeechGrammarRules extends Com4jObject,Iterable<Com4jObject> {
    /**
     * Count
     */
    @VTID(7)
    int count();

    /**
     * FindRule
     */
    @VTID(8)
    org.daisy.util.wincom.sapi5.ISpeechGrammarRule findRule(
        @MarshalAs(NativeType.VARIANT) java.lang.Object ruleNameOrId);

    /**
     * Item
     */
    @VTID(9)
    @DefaultMethod
    org.daisy.util.wincom.sapi5.ISpeechGrammarRule item(
        int index);

    /**
     * Enumerates the alternates
     */
    @VTID(10)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Dynamic
     */
    @VTID(11)
    boolean dynamic();

    /**
     * Add
     */
    @VTID(12)
    org.daisy.util.wincom.sapi5.ISpeechGrammarRule add(
        java.lang.String ruleName,
        org.daisy.util.wincom.sapi5.SpeechRuleAttributes attributes,
        @DefaultValue("0")int ruleId);

    /**
     * Commit
     */
    @VTID(13)
    void commit();

    /**
     * CommitAndSave
     */
    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object commitAndSave(
        Holder<java.lang.String> errorText);

}
