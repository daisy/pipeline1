package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 */
public enum SPSHORTCUTTYPE implements ComEnum {
  /**
   * <p>
   * The value of this constant is -1
   * </p>
   */
  SPSHT_NotOverriden(-1),
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SPSHT_Unknown(0),
  /**
   * <p>
   * The value of this constant is 4096
   * </p>
   */
  SPSHT_EMAIL(4096),
  /**
   * <p>
   * The value of this constant is 8192
   * </p>
   */
  SPSHT_OTHER(8192),
  /**
   * <p>
   * The value of this constant is 12288
   * </p>
   */
  SPPS_RESERVED1(12288),
  /**
   * <p>
   * The value of this constant is 16384
   * </p>
   */
  SPPS_RESERVED2(16384),
  /**
   * <p>
   * The value of this constant is 20480
   * </p>
   */
  SPPS_RESERVED3(20480),
  /**
   * <p>
   * The value of this constant is 61440
   * </p>
   */
  SPPS_RESERVED4(61440),
  ;

  private final int value;
  SPSHORTCUTTYPE(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
