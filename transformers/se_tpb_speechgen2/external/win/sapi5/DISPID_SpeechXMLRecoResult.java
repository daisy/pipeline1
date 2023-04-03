package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechXMLRecoResult implements ComEnum {
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SRRGetXMLResult(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SRRGetXMLErrorInfo(11),
  ;

  private final int value;
  DISPID_SpeechXMLRecoResult(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
