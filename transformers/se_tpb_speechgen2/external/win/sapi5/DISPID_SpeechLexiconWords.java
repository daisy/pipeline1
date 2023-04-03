package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechLexiconWords implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SLWsCount(1),
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  DISPID_SLWsItem(0),
  /**
   * <p>
   * The value of this constant is -4
   * </p>
   */
  DISPID_SLWs_NewEnum(-4),
  ;

  private final int value;
  DISPID_SpeechLexiconWords(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
