package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpXMLRecoResult Interface
 */
@IID("{AE39362B-45A8-4074-9B9E-CCF49AA2D0B6}")
public interface ISpXMLRecoResult extends se_tpb_speechgen2.external.win.sapi5.ISpRecoResult {
  // Methods:
  /**
   * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPXMLRESULTOPTIONS parameter.
   * @return  Returns a value of type java.lang.String
   */

  @VTID(14)
  @ReturnValue(type=NativeType.Unicode,index=0)
  java.lang.String getXMLResult(
    se_tpb_speechgen2.external.win.sapi5.SPXMLRESULTOPTIONS options);


  // Properties:
}
