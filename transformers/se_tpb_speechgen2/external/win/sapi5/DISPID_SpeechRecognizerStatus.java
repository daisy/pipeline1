package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechRecognizerStatus implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SRSAudioStatus(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SRSCurrentStreamPosition(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SRSCurrentStreamNumber(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SRSNumberOfActiveRules(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SRSClsidEngine(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SRSSupportedLanguages(6),
  ;

  private final int value;
  DISPID_SpeechRecognizerStatus(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
