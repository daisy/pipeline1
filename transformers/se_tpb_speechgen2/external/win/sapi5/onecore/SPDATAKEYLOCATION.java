package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 */
public enum SPDATAKEYLOCATION implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SPDKL_DefaultLocation(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SPDKL_CurrentUser(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SPDKL_LocalMachine(2),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  SPDKL_CurrentConfig(5),
  ;

  private final int value;
  SPDATAKEYLOCATION(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
