package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechWaveFormatEx implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SWFEFormatTag(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SWFEChannels(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SWFESamplesPerSec(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SWFEAvgBytesPerSec(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SWFEBlockAlign(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SWFEBitsPerSample(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SWFEExtraData(7),
  ;

  private final int value;
  DISPID_SpeechWaveFormatEx(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
