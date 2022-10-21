package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechXMLRecoResult Interface
 */
@IID("{AAEC54AF-8F85-4924-944D-B79D39D72E19}")
public interface ISpeechXMLRecoResult extends se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult {
  // Methods:
  /**
   * <p>
   * GetXMLResult
   * </p>
   * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPXMLRESULTOPTIONS parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(17)
  java.lang.String getXMLResult(
    se_tpb_speechgen2.external.win.sapi5.SPXMLRESULTOPTIONS options);


  /**
   * <p>
   * GetXMLErrorInfo
   * </p>
   * @param lineNumber Mandatory Holder<Integer> parameter.
   * @param scriptLine Mandatory Holder<java.lang.String> parameter.
   * @param source Mandatory Holder<java.lang.String> parameter.
   * @param description Mandatory Holder<java.lang.String> parameter.
   * @param resultCode Mandatory Holder<Integer> parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(18)
  boolean getXMLErrorInfo(
    Holder<Integer> lineNumber,
    Holder<java.lang.String> scriptLine,
    Holder<java.lang.String> source,
    Holder<java.lang.String> description,
    Holder<Integer> resultCode);


  // Properties:
}
