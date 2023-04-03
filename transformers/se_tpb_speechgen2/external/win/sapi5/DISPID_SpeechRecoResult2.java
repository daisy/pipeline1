package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechRecoResult2 implements ComEnum {
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SRRSetTextFeedback(12),
  ;

  private final int value;
  DISPID_SpeechRecoResult2(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
