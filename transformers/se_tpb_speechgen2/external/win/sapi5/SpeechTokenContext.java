package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechTokenContext implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  STCInprocServer(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  STCInprocHandler(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  STCLocalServer(4),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  STCRemoteServer(16),
  /**
   * <p>
   * The value of this constant is 23
   * </p>
   */
  STCAll(23),
  ;

  private final int value;
  SpeechTokenContext(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
