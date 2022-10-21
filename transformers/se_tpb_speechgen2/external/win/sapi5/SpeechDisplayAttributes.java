package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechDisplayAttributes implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SDA_No_Trailing_Space(0),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SDA_One_Trailing_Space(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SDA_Two_Trailing_Spaces(4),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SDA_Consume_Leading_Spaces(8),
  ;

  private final int value;
  SpeechDisplayAttributes(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
