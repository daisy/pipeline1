package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechRecognizer implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SRRecognizer(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SRAllowAudioInputFormatChangesOnNextSet(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SRAudioInput(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SRAudioInputStream(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SRIsShared(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SRState(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SRStatus(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SRProfile(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SREmulateRecognition(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  DISPID_SRCreateRecoContext(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DISPID_SRGetFormat(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  DISPID_SRSetPropertyNumber(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DISPID_SRGetPropertyNumber(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  DISPID_SRSetPropertyString(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  DISPID_SRGetPropertyString(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  DISPID_SRIsUISupported(16),
  /**
   * <p>
   * The value of this constant is 17
   * </p>
   */
  DISPID_SRDisplayUI(17),
  /**
   * <p>
   * The value of this constant is 18
   * </p>
   */
  DISPID_SRGetRecognizers(18),
  /**
   * <p>
   * The value of this constant is 19
   * </p>
   */
  DISPID_SVGetAudioInputs(19),
  /**
   * <p>
   * The value of this constant is 20
   * </p>
   */
  DISPID_SVGetProfiles(20),
  ;

  private final int value;
  DISPID_SpeechRecognizer(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
