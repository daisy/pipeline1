package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechMemoryStream implements ComEnum {
  /**
   * <p>
   * The value of this constant is 100
   * </p>
   */
  DISPID_SMSSetData(100),
  /**
   * <p>
   * The value of this constant is 101
   * </p>
   */
  DISPID_SMSGetData(101),
  ;

  private final int value;
  DISPID_SpeechMemoryStream(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
