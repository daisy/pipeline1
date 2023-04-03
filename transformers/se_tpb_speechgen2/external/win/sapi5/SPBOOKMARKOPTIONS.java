package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SPBOOKMARKOPTIONS implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SPBO_NONE(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SPBO_PAUSE(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SPBO_AHEAD(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SPBO_TIME_UNITS(4),
  ;

  private final int value;
  SPBOOKMARKOPTIONS(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
