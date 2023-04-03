package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechVoiceSpeakFlags implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SVSFDefault(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SVSFlagsAsync(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SVSFPurgeBeforeSpeak(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SVSFIsFilename(4),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SVSFIsXML(8),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  SVSFIsNotXML(16),
  /**
   * <p>
   * The value of this constant is 32
   * </p>
   */
  SVSFPersistXML(32),
  /**
   * <p>
   * The value of this constant is 64
   * </p>
   */
  SVSFNLPSpeakPunc(64),
  /**
   * <p>
   * The value of this constant is 128
   * </p>
   */
  SVSFParseSapi(128),
  /**
   * <p>
   * The value of this constant is 256
   * </p>
   */
  SVSFParseSsml(256),
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SVSFParseAutodetect(0),
  /**
   * <p>
   * The value of this constant is 64
   * </p>
   */
  SVSFNLPMask(64),
  /**
   * <p>
   * The value of this constant is 384
   * </p>
   */
  SVSFParseMask(384),
  /**
   * <p>
   * The value of this constant is 511
   * </p>
   */
  SVSFVoiceMask(511),
  /**
   * <p>
   * The value of this constant is -512
   * </p>
   */
  SVSFUnusedFlags(-512),
  ;

  private final int value;
  SpeechVoiceSpeakFlags(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
