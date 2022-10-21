package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechVoiceEvent implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SVEStreamStart(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SVEStreamEnd(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SVEVoiceChange(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SVEBookmark(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SVEWord(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SVEPhoneme(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SVESentenceBoundary(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SVEViseme(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SVEAudioLevel(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SVEEnginePrivate(10),
  ;

  private final int value;
  DISPID_SpeechVoiceEvent(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
