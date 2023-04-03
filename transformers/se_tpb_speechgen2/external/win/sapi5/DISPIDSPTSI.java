package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPIDSPTSI implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPIDSPTSI_ActiveOffset(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPIDSPTSI_ActiveLength(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPIDSPTSI_SelectionOffset(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPIDSPTSI_SelectionLength(4),
  ;

  private final int value;
  DISPIDSPTSI(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
