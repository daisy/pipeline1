package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SPGRAMMARSTATE implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SPGS_DISABLED(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SPGS_ENABLED(1),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  SPGS_EXCLUSIVE(3),
  ;

  private final int value;
  SPGRAMMARSTATE(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
