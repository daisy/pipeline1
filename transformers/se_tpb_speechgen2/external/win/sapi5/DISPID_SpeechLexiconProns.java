package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechLexiconProns implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SLPsCount(1),
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  DISPID_SLPsItem(0),
  /**
   * <p>
   * The value of this constant is -4
   * </p>
   */
  DISPID_SLPs_NewEnum(-4),
  ;

  private final int value;
  DISPID_SpeechLexiconProns(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
