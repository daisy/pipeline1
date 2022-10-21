package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum DISPID_SpeechPhraseProperty implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  DISPID_SPPName(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  DISPID_SPPId(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  DISPID_SPPValue(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  DISPID_SPPFirstElement(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  DISPID_SPPNumberOfElements(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  DISPID_SPPEngineConfidence(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  DISPID_SPPConfidence(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  DISPID_SPPParent(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  DISPID_SPPChildren(9),
  ;

  private final int value;
  DISPID_SpeechPhraseProperty(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
