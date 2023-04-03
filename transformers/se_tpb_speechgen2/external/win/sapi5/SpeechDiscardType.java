package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechDiscardType implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SDTProperty(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SDTReplacement(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SDTRule(4),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SDTDisplayText(8),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  SDTLexicalForm(16),
  /**
   * <p>
   * The value of this constant is 32
   * </p>
   */
  SDTPronunciation(32),
  /**
   * <p>
   * The value of this constant is 64
   * </p>
   */
  SDTAudio(64),
  /**
   * <p>
   * The value of this constant is 128
   * </p>
   */
  SDTAlternates(128),
  /**
   * <p>
   * The value of this constant is 255
   * </p>
   */
  SDTAll(255),
  ;

  private final int value;
  SpeechDiscardType(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
