package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechGrammarRuleStateTransition Interface
 */
@IID("{CAFD1DB1-41D1-4A06-9863-E2E81DA17A9A}")
public interface ISpeechGrammarRuleStateTransition extends Com4jObject {
    /**
     * Type
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.SpeechGrammarRuleStateTransitionType type();

    /**
     * Text
     */
    @VTID(8)
    java.lang.String text();

    /**
     * Rule
     */
    @VTID(9)
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule rule();

    /**
     * Weight
     */
    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object weight();

    /**
     * PropertyName
     */
    @VTID(11)
    java.lang.String propertyName();

    /**
     * PropertyId
     */
    @VTID(12)
    int propertyId();

    /**
     * PropertyValue
     */
    @VTID(13)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object propertyValue();

    /**
     * NextState
     */
    @VTID(14)
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState nextState();

}
