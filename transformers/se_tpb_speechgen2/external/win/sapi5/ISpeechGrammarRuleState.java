package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechGrammarRuleState Interface
 */
@IID("{D4286F2C-EE67-45AE-B928-28D695362EDA}")
public interface ISpeechGrammarRuleState extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Rule
   * </p>
   * <p>
   * Getter method for the COM property "Rule"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule rule();


  /**
   * <p>
   * Transitions
   * </p>
   * <p>
   * Getter method for the COM property "Transitions"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleStateTransitions
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleStateTransitions transitions();


  @VTID(8)
  @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleStateTransitions.class})
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleStateTransition transitions(
    int index);

  /**
   * <p>
   * AddWordTransition
   * </p>
   * @param destState Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState parameter.
   * @param words Mandatory java.lang.String parameter.
   * @param separators Optional parameter. Default value is " "
   * @param type Optional parameter. Default value is 1
   * @param propertyName Optional parameter. Default value is ""
   * @param propertyId Optional parameter. Default value is 0
   * @param propertyValue Optional parameter. Default value is ""
   * @param weight Optional parameter. Default value is 1.0f
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  void addWordTransition(
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState destState,
    java.lang.String words,
    @Optional @DefaultValue(" ") java.lang.String separators,
    @Optional @DefaultValue("1") se_tpb_speechgen2.external.win.sapi5.SpeechGrammarWordType type,
    @Optional @DefaultValue("") java.lang.String propertyName,
    @Optional @DefaultValue("0") int propertyId,
    @Optional @DefaultValue("") java.lang.Object propertyValue,
    @Optional @DefaultValue("1") float weight);


  /**
   * <p>
   * AddRuleTransition
   * </p>
   * @param destinationState Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState parameter.
   * @param rule Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule parameter.
   * @param propertyName Optional parameter. Default value is ""
   * @param propertyId Optional parameter. Default value is 0
   * @param propertyValue Optional parameter. Default value is ""
   * @param weight Optional parameter. Default value is 1.0f
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  void addRuleTransition(
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState destinationState,
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule rule,
    @Optional @DefaultValue("") java.lang.String propertyName,
    @Optional @DefaultValue("0") int propertyId,
    @Optional @DefaultValue("") java.lang.Object propertyValue,
    @Optional @DefaultValue("1") float weight);


  /**
   * <p>
   * AddSpecialTransition
   * </p>
   * @param destinationState Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState parameter.
   * @param type Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechSpecialTransitionType parameter.
   * @param propertyName Optional parameter. Default value is ""
   * @param propertyId Optional parameter. Default value is 0
   * @param propertyValue Optional parameter. Default value is ""
   * @param weight Optional parameter. Default value is 1.0f
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  void addSpecialTransition(
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState destinationState,
    se_tpb_speechgen2.external.win.sapi5.SpeechSpecialTransitionType type,
    @Optional @DefaultValue("") java.lang.String propertyName,
    @Optional @DefaultValue("0") int propertyId,
    @Optional @DefaultValue("") java.lang.Object propertyValue,
    @Optional @DefaultValue("1") float weight);


  // Properties:
}
