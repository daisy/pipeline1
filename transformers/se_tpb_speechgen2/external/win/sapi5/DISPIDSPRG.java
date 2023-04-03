package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPIDSPRG implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SRGId(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SRGRecoContext(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SRGState(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SRGRules(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SRGReset(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SRGCommit(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SRGCmdLoadFromFile(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SRGCmdLoadFromObject(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SRGCmdLoadFromResource(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SRGCmdLoadFromMemory(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SRGCmdLoadFromProprietaryGrammar(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SRGCmdSetRuleState(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DISPID_SRGCmdSetRuleIdState(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  DISPID_SRGDictationLoad(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  DISPID_SRGDictationUnload(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  DISPID_SRGDictationSetState(16),
  /**
   * <p>
   * The value of this constant is 17
   * </p>
   */
  DISPID_SRGSetWordSequenceData(17),
  /**
   * <p>
   * The value of this constant is 18
   * </p>
   */
  DISPID_SRGSetTextSelection(18),
  /**
   * <p>
   * The value of this constant is 19
   * </p>
   */
  DISPID_SRGIsPronounceable(19),
  ;

  private final int value;
  DISPIDSPRG(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
