package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechMMSysAudio implements ComEnum {
  /**
   * <p>
   * The value of this constant is 300
   * </p>
   */
  DISPID_SMSADeviceId(300),
  /**
   * <p>
   * The value of this constant is 301
   * </p>
   */
  DISPID_SMSALineId(301),
  /**
   * <p>
   * The value of this constant is 302
   * </p>
   */
  DISPID_SMSAMMHandle(302),
  ;

  private final int value;
  DISPID_SpeechMMSysAudio(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
