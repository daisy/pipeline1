package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechTokenShellFolder implements ComEnum {
  /**
   * <p>
   * The value of this constant is 26
   * </p>
   */
  STSF_AppData(26),
  /**
   * <p>
   * The value of this constant is 28
   * </p>
   */
  STSF_LocalAppData(28),
  /**
   * <p>
   * The value of this constant is 35
   * </p>
   */
  STSF_CommonAppData(35),
  /**
   * <p>
   * The value of this constant is 32768
   * </p>
   */
  STSF_FlagCreate(32768),
  ;

  private final int value;
  SpeechTokenShellFolder(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
