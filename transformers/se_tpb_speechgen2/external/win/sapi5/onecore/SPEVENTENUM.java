package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 */
public enum SPEVENTENUM implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SPEI_UNDEFINED(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SPEI_START_INPUT_STREAM(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SPEI_END_INPUT_STREAM(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  SPEI_VOICE_CHANGE(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SPEI_TTS_BOOKMARK(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  SPEI_WORD_BOUNDARY(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  SPEI_PHONEME(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  SPEI_SENTENCE_BOUNDARY(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SPEI_VISEME(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  SPEI_TTS_AUDIO_LEVEL(9),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  SPEI_TTS_PRIVATE(15),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SPEI_MIN_TTS(1),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  SPEI_MAX_TTS(15),
  /**
   * <p>
   * The value of this constant is 30
   * </p>
   */
  SPEI_FLAGCHECK1(30),
  /**
   * <p>
   * The value of this constant is 33
   * </p>
   */
  SPEI_FLAGCHECK2(33),
  /**
   * <p>
   * The value of this constant is 34
   * </p>
   */
  SPEI_END_SR_STREAM(34),
  /**
   * <p>
   * The value of this constant is 35
   * </p>
   */
  SPEI_SOUND_START(35),
  /**
   * <p>
   * The value of this constant is 36
   * </p>
   */
  SPEI_SOUND_END(36),
  /**
   * <p>
   * The value of this constant is 37
   * </p>
   */
  SPEI_PHRASE_START(37),
  /**
   * <p>
   * The value of this constant is 38
   * </p>
   */
  SPEI_RECOGNITION(38),
  /**
   * <p>
   * The value of this constant is 39
   * </p>
   */
  SPEI_HYPOTHESIS(39),
  /**
   * <p>
   * The value of this constant is 40
   * </p>
   */
  SPEI_SR_BOOKMARK(40),
  /**
   * <p>
   * The value of this constant is 41
   * </p>
   */
  SPEI_PROPERTY_NUM_CHANGE(41),
  /**
   * <p>
   * The value of this constant is 42
   * </p>
   */
  SPEI_PROPERTY_STRING_CHANGE(42),
  /**
   * <p>
   * The value of this constant is 43
   * </p>
   */
  SPEI_FALSE_RECOGNITION(43),
  /**
   * <p>
   * The value of this constant is 44
   * </p>
   */
  SPEI_INTERFERENCE(44),
  /**
   * <p>
   * The value of this constant is 45
   * </p>
   */
  SPEI_REQUEST_UI(45),
  /**
   * <p>
   * The value of this constant is 46
   * </p>
   */
  SPEI_RECO_STATE_CHANGE(46),
  /**
   * <p>
   * The value of this constant is 47
   * </p>
   */
  SPEI_ADAPTATION(47),
  /**
   * <p>
   * The value of this constant is 48
   * </p>
   */
  SPEI_START_SR_STREAM(48),
  /**
   * <p>
   * The value of this constant is 49
   * </p>
   */
  SPEI_RECO_OTHER_CONTEXT(49),
  /**
   * <p>
   * The value of this constant is 50
   * </p>
   */
  SPEI_SR_AUDIO_LEVEL(50),
  /**
   * <p>
   * The value of this constant is 51
   * </p>
   */
  SPEI_SR_RETAINEDAUDIO(51),
  /**
   * <p>
   * The value of this constant is 52
   * </p>
   */
  SPEI_SR_PRIVATE(52),
  /**
   * <p>
   * The value of this constant is 53
   * </p>
   */
  SPEI_ACTIVE_CATEGORY_CHANGED(53),
  /**
   * <p>
   * The value of this constant is 54
   * </p>
   */
  SPEI_TEXTFEEDBACK(54),
  /**
   * <p>
   * The value of this constant is 55
   * </p>
   */
  SPEI_RECOGNITION_ALL(55),
  /**
   * <p>
   * The value of this constant is 56
   * </p>
   */
  SPEI_BARGE_IN(56),
  /**
   * <p>
   * The value of this constant is 57
   * </p>
   */
  SPEI_TONE(57),
  /**
   * <p>
   * The value of this constant is 58
   * </p>
   */
  SPEI_CLOUD_STATUS(58),
  /**
   * <p>
   * The value of this constant is 59
   * </p>
   */
  SPEI_TENTATIVE(59),
  /**
   * <p>
   * The value of this constant is 60
   * </p>
   */
  SPEI_SHAREDRECO_DISCONNECTED(60),
  /**
   * <p>
   * The value of this constant is 61
   * </p>
   */
  SPEI_INPUT_FOCUS_CHANGE(61),
  /**
   * <p>
   * The value of this constant is 62
   * </p>
   */
  SPEI_CLOUD_RECOGNITION(62),
  /**
   * <p>
   * The value of this constant is 34
   * </p>
   */
  SPEI_MIN_SR(34),
  /**
   * <p>
   * The value of this constant is 62
   * </p>
   */
  SPEI_MAX_SR(62),
  ;

  private final int value;
  SPEVENTENUM(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
