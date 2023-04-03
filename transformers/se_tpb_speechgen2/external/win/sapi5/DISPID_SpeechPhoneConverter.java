package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechPhoneConverter implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SPCLangId(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SPCPhoneToId(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SPCIdToPhone(3),
  ;

  private final int value;
  DISPID_SpeechPhoneConverter(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
