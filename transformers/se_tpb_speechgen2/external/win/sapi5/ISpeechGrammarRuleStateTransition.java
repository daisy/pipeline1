package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechGrammarRuleStateTransition Interface
 */
@IID("{CAFD1DB1-41D1-4A06-9863-E2E81DA17A9A}")
public interface ISpeechGrammarRuleStateTransition extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Type
   * </p>
   * <p>
   * Getter method for the COM property "Type"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechGrammarRuleStateTransitionType
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.SpeechGrammarRuleStateTransitionType type();


  /**
   * <p>
   * Text
   * </p>
   * <p>
   * Getter method for the COM property "Text"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String text();


  /**
   * <p>
   * Rule
   * </p>
   * <p>
   * Getter method for the COM property "Rule"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule rule();


  /**
   * <p>
   * Weight
   * </p>
   * <p>
   * Getter method for the COM property "Weight"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object weight();


  /**
   * <p>
   * PropertyName
   * </p>
   * <p>
   * Getter method for the COM property "PropertyName"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  java.lang.String propertyName();


  /**
   * <p>
   * PropertyId
   * </p>
   * <p>
   * Getter method for the COM property "PropertyId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  int propertyId();


  /**
   * <p>
   * PropertyValue
   * </p>
   * <p>
   * Getter method for the COM property "PropertyValue"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object propertyValue();


  /**
   * <p>
   * NextState
   * </p>
   * <p>
   * Getter method for the COM property "NextState"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState nextState();


  // Properties:
}
