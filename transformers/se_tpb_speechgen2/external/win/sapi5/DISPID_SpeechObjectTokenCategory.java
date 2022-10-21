package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechObjectTokenCategory implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SOTCId(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SOTCDefault(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SOTCSetId(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SOTCGetDataKey(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SOTCEnumerateTokens(5),
  ;

  private final int value;
  DISPID_SpeechObjectTokenCategory(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
