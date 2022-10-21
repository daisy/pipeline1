package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechAudioBufferInfo implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SABIMinNotification(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SABIBufferSize(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SABIEventBias(3),
  ;

  private final int value;
  DISPID_SpeechAudioBufferInfo(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
