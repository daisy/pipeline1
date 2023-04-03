package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechLexicon implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SLGenerationId(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SLGetWords(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SLAddPronunciation(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SLAddPronunciationByPhoneIds(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SLRemovePronunciation(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SLRemovePronunciationByPhoneIds(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SLGetPronunciations(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SLGetGenerationChange(8),
  ;

  private final int value;
  DISPID_SpeechLexicon(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
