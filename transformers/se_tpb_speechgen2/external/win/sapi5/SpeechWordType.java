package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechWordType implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SWTAdded(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SWTDeleted(2),
  ;

  private final int value;
  SpeechWordType(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
