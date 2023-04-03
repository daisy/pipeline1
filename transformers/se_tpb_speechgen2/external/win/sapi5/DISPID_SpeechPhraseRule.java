package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechPhraseRule implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SPRuleName(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SPRuleId(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SPRuleFirstElement(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SPRuleNumberOfElements(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SPRuleParent(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SPRuleChildren(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SPRuleConfidence(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SPRuleEngineConfidence(8),
  ;

  private final int value;
  DISPID_SpeechPhraseRule(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
