package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechRuleAttributes implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SRATopLevel(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SRADefaultToActive(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SRAExport(4),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SRAImport(8),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  SRAInterpreter(16),
  /**
   * <p>
   * The value of this constant is 32
   * </p>
   */
  SRADynamic(32),
  /**
   * <p>
   * The value of this constant is 64
   * </p>
   */
  SRARoot(64),
  ;

  private final int value;
  SpeechRuleAttributes(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
