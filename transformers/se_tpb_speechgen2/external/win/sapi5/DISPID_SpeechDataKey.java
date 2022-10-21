package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechDataKey implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SDKSetBinaryValue(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SDKGetBinaryValue(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SDKSetStringValue(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SDKGetStringValue(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SDKSetLongValue(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SDKGetlongValue(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SDKOpenKey(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SDKCreateKey(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SDKDeleteKey(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SDKDeleteValue(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SDKEnumKeys(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SDKEnumValues(12),
  ;

  private final int value;
  DISPID_SpeechDataKey(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
