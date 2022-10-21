package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SPSEMANTICFORMAT implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SPSMF_SAPI_PROPERTIES(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SPSMF_SRGS_SEMANTICINTERPRETATION_MS(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SPSMF_SRGS_SAPIPROPERTIES(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SPSMF_UPS(4),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SPSMF_SRGS_SEMANTICINTERPRETATION_W3C(8),
  ;

  private final int value;
  SPSEMANTICFORMAT(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
