package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SPWORDTYPE implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  eWORDTYPE_ADDED(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  eWORDTYPE_DELETED(2),
  ;

  private final int value;
  SPWORDTYPE(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
