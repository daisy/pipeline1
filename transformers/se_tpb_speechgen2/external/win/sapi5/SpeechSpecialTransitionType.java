package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechSpecialTransitionType implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SSTTWildcard(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SSTTDictation(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  SSTTTextBuffer(3),
  ;

  private final int value;
  SpeechSpecialTransitionType(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
