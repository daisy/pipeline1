package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 */
public enum SPPREDICTORTYPE implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  PRED_DATA_TYPE_BOOL(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  PRED_DATA_TYPE_CHAR(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  PRED_DATA_TYPE_WCHAR(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  PRED_DATA_TYPE_SHORT(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  PRED_DATA_TYPE_USHORT(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  PRED_DATA_TYPE_INT(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  PRED_DATA_TYPE_UINT(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  PRED_DATA_TYPE_LONGLONG(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  PRED_DATA_TYPE_ULONGLONG(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  PRED_DATA_TYPE_FLOAT(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  PRED_DATA_TYPE_DOUBLE(11),
  ;

  private final int value;
  SPPREDICTORTYPE(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
