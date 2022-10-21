package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechObjectToken implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SOTId(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SOTDataKey(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SOTCategory(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SOTGetDescription(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SOTSetId(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SOTGetAttribute(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SOTCreateInstance(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SOTRemove(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SOTGetStorageFileName(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SOTRemoveStorageFileName(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SOTIsUISupported(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SOTDisplayUI(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DISPID_SOTMatchesAttributes(13),
  ;

  private final int value;
  DISPID_SpeechObjectToken(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
