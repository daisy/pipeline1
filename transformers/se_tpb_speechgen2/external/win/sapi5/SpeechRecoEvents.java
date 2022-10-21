package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechRecoEvents implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SREStreamEnd(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SRESoundStart(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SRESoundEnd(4),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SREPhraseStart(8),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  SRERecognition(16),
  /**
   * <p>
   * The value of this constant is 32
   * </p>
   */
  SREHypothesis(32),
  /**
   * <p>
   * The value of this constant is 64
   * </p>
   */
  SREBookmark(64),
  /**
   * <p>
   * The value of this constant is 128
   * </p>
   */
  SREPropertyNumChange(128),
  /**
   * <p>
   * The value of this constant is 256
   * </p>
   */
  SREPropertyStringChange(256),
  /**
   * <p>
   * The value of this constant is 512
   * </p>
   */
  SREFalseRecognition(512),
  /**
   * <p>
   * The value of this constant is 1024
   * </p>
   */
  SREInterference(1024),
  /**
   * <p>
   * The value of this constant is 2048
   * </p>
   */
  SRERequestUI(2048),
  /**
   * <p>
   * The value of this constant is 4096
   * </p>
   */
  SREStateChange(4096),
  /**
   * <p>
   * The value of this constant is 8192
   * </p>
   */
  SREAdaptation(8192),
  /**
   * <p>
   * The value of this constant is 16384
   * </p>
   */
  SREStreamStart(16384),
  /**
   * <p>
   * The value of this constant is 32768
   * </p>
   */
  SRERecoOtherContext(32768),
  /**
   * <p>
   * The value of this constant is 65536
   * </p>
   */
  SREAudioLevel(65536),
  /**
   * <p>
   * The value of this constant is 262144
   * </p>
   */
  SREPrivate(262144),
  /**
   * <p>
   * The value of this constant is 393215
   * </p>
   */
  SREAllEvents(393215),
  ;

  private final int value;
  SpeechRecoEvents(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
