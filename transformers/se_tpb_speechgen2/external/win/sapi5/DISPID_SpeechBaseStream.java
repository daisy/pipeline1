package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechBaseStream implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SBSFormat(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SBSRead(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SBSWrite(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SBSSeek(4),
  ;

  private final int value;
  DISPID_SpeechBaseStream(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
