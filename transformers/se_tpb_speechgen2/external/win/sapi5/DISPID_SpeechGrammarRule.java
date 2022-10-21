package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechGrammarRule implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SGRAttributes(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SGRInitialState(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SGRName(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SGRId(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SGRClear(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SGRAddResource(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SGRAddState(7),
  ;

  private final int value;
  DISPID_SpeechGrammarRule(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
