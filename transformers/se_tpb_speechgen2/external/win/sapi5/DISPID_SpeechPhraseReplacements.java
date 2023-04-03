package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechPhraseReplacements implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SPRsCount(1),
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  DISPID_SPRsItem(0),
  /**
   * <p>
   * The value of this constant is -4
   * </p>
   */
  DISPID_SPRs_NewEnum(-4),
  ;

  private final int value;
  DISPID_SpeechPhraseReplacements(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
