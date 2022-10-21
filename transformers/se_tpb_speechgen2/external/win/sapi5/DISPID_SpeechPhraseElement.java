package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechPhraseElement implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SPEAudioTimeOffset(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SPEAudioSizeTime(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SPEAudioStreamOffset(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SPEAudioSizeBytes(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SPERetainedStreamOffset(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SPERetainedSizeBytes(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SPEDisplayText(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SPELexicalForm(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SPEPronunciation(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SPEDisplayAttributes(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SPERequiredConfidence(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SPEActualConfidence(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DISPID_SPEEngineConfidence(13),
  ;

  private final int value;
  DISPID_SpeechPhraseElement(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
