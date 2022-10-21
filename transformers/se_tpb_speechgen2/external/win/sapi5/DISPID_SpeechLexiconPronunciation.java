package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechLexiconPronunciation implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SLPType(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SLPLangId(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SLPPartOfSpeech(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SLPPhoneIds(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SLPSymbolic(5),
  ;

  private final int value;
  DISPID_SpeechLexiconPronunciation(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
