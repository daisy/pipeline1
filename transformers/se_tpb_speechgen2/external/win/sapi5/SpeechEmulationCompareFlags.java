package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechEmulationCompareFlags implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SECFIgnoreCase(1),
  /**
   * <p>
   * The value of this constant is 65536
   * </p>
   */
  SECFIgnoreKanaType(65536),
  /**
   * <p>
   * The value of this constant is 131072
   * </p>
   */
  SECFIgnoreWidth(131072),
  /**
   * <p>
   * The value of this constant is 536870912
   * </p>
   */
  SECFNoSpecialChars(536870912),
  /**
   * <p>
   * The value of this constant is 1073741824
   * </p>
   */
  SECFEmulateResult(1073741824),
  /**
   * <p>
   * The value of this constant is 196609
   * </p>
   */
  SECFDefault(196609),
  ;

  private final int value;
  SpeechEmulationCompareFlags(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
