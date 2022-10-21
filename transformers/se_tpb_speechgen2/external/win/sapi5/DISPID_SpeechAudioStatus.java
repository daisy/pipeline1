package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechAudioStatus implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SASFreeBufferSpace(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SASNonBlockingIO(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SASState(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SASCurrentSeekPosition(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SASCurrentDevicePosition(5),
  ;

  private final int value;
  DISPID_SpeechAudioStatus(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
