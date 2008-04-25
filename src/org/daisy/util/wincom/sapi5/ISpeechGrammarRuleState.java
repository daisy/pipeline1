package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechGrammarRuleState Interface
 */
@IID("{D4286F2C-EE67-45AE-B928-28D695362EDA}")
public interface ISpeechGrammarRuleState extends Com4jObject {
    /**
     * Rule
     */
    @VTID(7)
    org.daisy.util.wincom.sapi5.ISpeechGrammarRule rule();

    /**
     * Transitions
     */
    @VTID(8)
    org.daisy.util.wincom.sapi5.ISpeechGrammarRuleStateTransitions transitions();

    @VTID(8)
    @ReturnValue(defaultPropertyThrough={org.daisy.util.wincom.sapi5.ISpeechGrammarRuleStateTransitions.class})
    org.daisy.util.wincom.sapi5.ISpeechGrammarRuleStateTransition transitions(
        int index);

    /**
     * AddWordTransition
     */
    @VTID(9)
    void addWordTransition(
        org.daisy.util.wincom.sapi5.ISpeechGrammarRuleState destState,
        java.lang.String words,
        @DefaultValue(" ")java.lang.String separators,
        @DefaultValue("1")org.daisy.util.wincom.sapi5.SpeechGrammarWordType type,
        @DefaultValue("")java.lang.String propertyName,
        @DefaultValue("0")int propertyId,
        @DefaultValue("")java.lang.Object propertyValue,
        @DefaultValue("1")float weight);

    /**
     * AddRuleTransition
     */
    @VTID(10)
    void addRuleTransition(
        org.daisy.util.wincom.sapi5.ISpeechGrammarRuleState destinationState,
        org.daisy.util.wincom.sapi5.ISpeechGrammarRule rule,
        @DefaultValue("")java.lang.String propertyName,
        @DefaultValue("0")int propertyId,
        @DefaultValue("")java.lang.Object propertyValue,
        @DefaultValue("1")float weight);

    /**
     * AddSpecialTransition
     */
    @VTID(11)
    void addSpecialTransition(
        org.daisy.util.wincom.sapi5.ISpeechGrammarRuleState destinationState,
        org.daisy.util.wincom.sapi5.SpeechSpecialTransitionType type,
        @DefaultValue("")java.lang.String propertyName,
        @DefaultValue("0")int propertyId,
        @DefaultValue("")java.lang.Object propertyValue,
        @DefaultValue("1")float weight);

}
