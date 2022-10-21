package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechLexiconWord implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SLWLangId(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SLWType(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SLWWord(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SLWPronunciations(4),
  ;

  private final int value;
  DISPID_SpeechLexiconWord(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
