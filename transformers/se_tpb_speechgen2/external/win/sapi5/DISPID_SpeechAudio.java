package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechAudio implements ComEnum {
  /**
   * <p>
   * The value of this constant is 200
   * </p>
   */
  DISPID_SAStatus(200),
  /**
   * <p>
   * The value of this constant is 201
   * </p>
   */
  DISPID_SABufferInfo(201),
  /**
   * <p>
   * The value of this constant is 202
   * </p>
   */
  DISPID_SADefaultFormat(202),
  /**
   * <p>
   * The value of this constant is 203
   * </p>
   */
  DISPID_SAVolume(203),
  /**
   * <p>
   * The value of this constant is 204
   * </p>
   */
  DISPID_SABufferNotifySize(204),
  /**
   * <p>
   * The value of this constant is 205
   * </p>
   */
  DISPID_SAEventHandle(205),
  /**
   * <p>
   * The value of this constant is 206
   * </p>
   */
  DISPID_SASetState(206),
  ;

  private final int value;
  DISPID_SpeechAudio(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
