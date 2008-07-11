package se_tpb_speechgen2.external.win.sapi5  ;

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
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule rule();

    /**
     * Transitions
     */
    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleStateTransitions transitions();

    @VTID(8)
    @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleStateTransitions.class})
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleStateTransition transitions(
        int index);

    /**
     * AddWordTransition
     */
    @VTID(9)
    void addWordTransition(
        se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState destState,
        java.lang.String words,
        @DefaultValue(" ")java.lang.String separators,
        @DefaultValue("1")se_tpb_speechgen2.external.win.sapi5.SpeechGrammarWordType type,
        @DefaultValue("")java.lang.String propertyName,
        @DefaultValue("0")int propertyId,
        @DefaultValue("")java.lang.Object propertyValue,
        @DefaultValue("1")float weight);

    /**
     * AddRuleTransition
     */
    @VTID(10)
    void addRuleTransition(
        se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState destinationState,
        se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule rule,
        @DefaultValue("")java.lang.String propertyName,
        @DefaultValue("0")int propertyId,
        @DefaultValue("")java.lang.Object propertyValue,
        @DefaultValue("1")float weight);

    /**
     * AddSpecialTransition
     */
    @VTID(11)
    void addSpecialTransition(
        se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState destinationState,
        se_tpb_speechgen2.external.win.sapi5.SpeechSpecialTransitionType type,
        @DefaultValue("")java.lang.String propertyName,
        @DefaultValue("0")int propertyId,
        @DefaultValue("")java.lang.Object propertyValue,
        @DefaultValue("1")float weight);

}
