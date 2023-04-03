package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechRunState implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SRSEDone(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SRSEIsSpeaking(2),
  ;

  private final int value;
  SpeechRunState(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
