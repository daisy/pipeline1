package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechPhraseInfo implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SPILanguageId(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SPIGrammarId(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SPIStartTime(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SPIAudioStreamPosition(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SPIAudioSizeBytes(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SPIRetainedSizeBytes(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SPIAudioSizeTime(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SPIRule(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SPIProperties(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SPIElements(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SPIReplacements(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SPIEngineId(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DISPID_SPIEnginePrivateData(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  DISPID_SPISaveToMemory(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  DISPID_SPIGetText(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  DISPID_SPIGetDisplayAttributes(16),
  ;

  private final int value;
  DISPID_SpeechPhraseInfo(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
