package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechLexiconType implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SLTUser(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SLTApp(2),
  ;

  private final int value;
  SpeechLexiconType(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
