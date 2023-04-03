package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechRecoContextEvents implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SRCEStartStream(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SRCEEndStream(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SRCEBookmark(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SRCESoundStart(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SRCESoundEnd(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SRCEPhraseStart(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SRCERecognition(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SRCEHypothesis(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SRCEPropertyNumberChange(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SRCEPropertyStringChange(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SRCEFalseRecognition(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SRCEInterference(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DISPID_SRCERequestUI(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  DISPID_SRCERecognizerStateChange(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  DISPID_SRCEAdaptation(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  DISPID_SRCERecognitionForOtherContext(16),
  /**
   * <p>
   * The value of this constant is 17
   * </p>
   */
  DISPID_SRCEAudioLevel(17),
  /**
   * <p>
   * The value of this constant is 18
   * </p>
   */
  DISPID_SRCEEnginePrivate(18),
  ;

  private final int value;
  DISPID_SpeechRecoContextEvents(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
