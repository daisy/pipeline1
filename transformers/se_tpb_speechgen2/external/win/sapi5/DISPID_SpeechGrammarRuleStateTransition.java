package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechGrammarRuleStateTransition implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SGRSTType(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SGRSTText(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SGRSTRule(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SGRSTWeight(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SGRSTPropertyName(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SGRSTPropertyId(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SGRSTPropertyValue(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SGRSTNextState(8),
  ;

  private final int value;
  DISPID_SpeechGrammarRuleStateTransition(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
