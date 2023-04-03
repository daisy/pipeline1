package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechPhraseAlternate implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SPARecoResult(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SPAStartElementInResult(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SPANumberOfElementsInResult(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SPAPhraseInfo(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SPACommit(5),
  ;

  private final int value;
  DISPID_SpeechPhraseAlternate(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
