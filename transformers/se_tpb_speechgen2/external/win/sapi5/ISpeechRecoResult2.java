package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecoResult2 Interface
 */
@IID("{8E0A246D-D3C8-45DE-8657-04290C458C3C}")
public interface ISpeechRecoResult2 extends se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult {
  // Methods:
  /**
   * <p>
   * DiscardResultInfo
   * </p>
   * @param feedback Mandatory java.lang.String parameter.
   * @param wasSuccessful Mandatory boolean parameter.
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(17)
  void setTextFeedback(
    java.lang.String feedback,
    boolean wasSuccessful);


  // Properties:
}
