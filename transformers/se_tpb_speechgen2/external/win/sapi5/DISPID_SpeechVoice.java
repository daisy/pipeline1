package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechVoice implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SVStatus(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SVVoice(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SVAudioOutput(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SVAudioOutputStream(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SVRate(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SVVolume(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SVAllowAudioOuputFormatChangesOnNextSet(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SVEventInterests(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SVPriority(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SVAlertBoundary(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SVSyncronousSpeakTimeout(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SVSpeak(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DISPID_SVSpeakStream(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  DISPID_SVPause(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  DISPID_SVResume(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  DISPID_SVSkip(16),
  /**
   * <p>
   * The value of this constant is 17
   * </p>
   */
  DISPID_SVGetVoices(17),
  /**
   * <p>
   * The value of this constant is 18
   * </p>
   */
  DISPID_SVGetAudioOutputs(18),
  /**
   * <p>
   * The value of this constant is 19
   * </p>
   */
  DISPID_SVWaitUntilDone(19),
  /**
   * <p>
   * The value of this constant is 20
   * </p>
   */
  DISPID_SVSpeakCompleteEvent(20),
  /**
   * <p>
   * The value of this constant is 21
   * </p>
   */
  DISPID_SVIsUISupported(21),
  /**
   * <p>
   * The value of this constant is 22
   * </p>
   */
  DISPID_SVDisplayUI(22),
  ;

  private final int value;
  DISPID_SpeechVoice(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
