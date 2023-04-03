package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechPhraseReplacement implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SPRDisplayAttributes(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SPRText(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SPRFirstElement(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SPRNumberOfElements(4),
  ;

  private final int value;
  DISPID_SpeechPhraseReplacement(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
