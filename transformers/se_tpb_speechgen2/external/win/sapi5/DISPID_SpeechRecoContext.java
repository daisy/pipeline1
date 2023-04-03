package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechRecoContext implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SRCRecognizer(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SRCAudioInInterferenceStatus(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SRCRequestedUIType(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SRCVoice(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SRAllowVoiceFormatMatchingOnNextSet(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SRCVoicePurgeEvent(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SRCEventInterests(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SRCCmdMaxAlternates(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SRCState(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SRCRetainedAudio(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SRCRetainedAudioFormat(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SRCPause(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DISPID_SRCResume(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  DISPID_SRCCreateGrammar(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  DISPID_SRCCreateResultFromMemory(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  DISPID_SRCBookmark(16),
  /**
   * <p>
   * The value of this constant is 17
   * </p>
   */
  DISPID_SRCSetAdaptationData(17),
  ;

  private final int value;
  DISPID_SpeechRecoContext(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
