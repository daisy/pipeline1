package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechGrammarRules implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SGRsCount(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SGRsDynamic(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SGRsAdd(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SGRsCommit(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SGRsCommitAndSave(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SGRsFindRule(6),
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  DISPID_SGRsItem(0),
  /**
   * <p>
   * The value of this constant is -4
   * </p>
   */
  DISPID_SGRs_NewEnum(-4),
  ;

  private final int value;
  DISPID_SpeechGrammarRules(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
