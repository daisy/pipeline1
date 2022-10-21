package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechVoiceStatus implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SVSCurrentStreamNumber(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SVSLastStreamNumberQueued(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SVSLastResult(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SVSRunningState(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SVSInputWordPosition(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SVSInputWordLength(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SVSInputSentencePosition(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SVSInputSentenceLength(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SVSLastBookmark(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SVSLastBookmarkId(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SVSPhonemeId(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SVSVisemeId(12),
  ;

  private final int value;
  DISPID_SpeechVoiceStatus(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
