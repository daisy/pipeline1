package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechVoiceEvents implements ComEnum {
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SVEStartInputStream(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SVEEndInputStream(4),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SVEVoiceChange(8),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  SVEBookmark(16),
  /**
   * <p>
   * The value of this constant is 32
   * </p>
   */
  SVEWordBoundary(32),
  /**
   * <p>
   * The value of this constant is 64
   * </p>
   */
  SVEPhoneme(64),
  /**
   * <p>
   * The value of this constant is 128
   * </p>
   */
  SVESentenceBoundary(128),
  /**
   * <p>
   * The value of this constant is 256
   * </p>
   */
  SVEViseme(256),
  /**
   * <p>
   * The value of this constant is 512
   * </p>
   */
  SVEAudioLevel(512),
  /**
   * <p>
   * The value of this constant is 32768
   * </p>
   */
  SVEPrivate(32768),
  /**
   * <p>
   * The value of this constant is 33790
   * </p>
   */
  SVEAllEvents(33790),
  ;

  private final int value;
  SpeechVoiceEvents(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
