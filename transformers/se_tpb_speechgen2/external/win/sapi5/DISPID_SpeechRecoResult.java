package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechRecoResult implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SRRRecoContext(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SRRTimes(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SRRAudioFormat(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SRRPhraseInfo(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SRRAlternates(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SRRAudio(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SRRSpeakAudio(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SRRSaveToMemory(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SRRDiscardResultInfo(9),
  ;

  private final int value;
  DISPID_SpeechRecoResult(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
