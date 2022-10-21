package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechEngineConfidence implements ComEnum {
  /**
   * <p>
   * The value of this constant is -1
   * </p>
   */
  SECLowConfidence(-1),
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SECNormalConfidence(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SECHighConfidence(1),
  ;

  private final int value;
  SpeechEngineConfidence(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
