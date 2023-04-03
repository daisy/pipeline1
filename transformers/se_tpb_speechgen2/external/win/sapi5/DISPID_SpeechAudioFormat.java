package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechAudioFormat implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SAFType(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SAFGuid(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SAFGetWaveFormatEx(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SAFSetWaveFormatEx(4),
  ;

  private final int value;
  DISPID_SpeechAudioFormat(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
