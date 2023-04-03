package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechGrammarRuleState implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SGRSRule(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SGRSTransitions(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SGRSAddWordTransition(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SGRSAddRuleTransition(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SGRSAddSpecialTransition(5),
  ;

  private final int value;
  DISPID_SpeechGrammarRuleState(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
