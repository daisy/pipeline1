package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpeechResourceLoader Interface
 */
@IID("{C3353227-F2E2-4AC7-AD1C-2786E16F98FB}")
public interface ISpeechResourceLoader extends Com4jObject {
  // Methods:
  /**
   * @param bstrResourceUri Mandatory java.lang.String parameter.
   * @param fAlwaysReload Mandatory boolean parameter.
   * @param pStream Mandatory Holder<com4j.Com4jObject> parameter.
   * @param pbstrMIMEType Mandatory Holder<java.lang.String> parameter.
   * @param pfModified Mandatory Holder<Boolean> parameter.
   * @param pbstrRedirectUrl Mandatory Holder<java.lang.String> parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  void loadResource(
    java.lang.String bstrResourceUri,
    boolean fAlwaysReload,
    Holder<com4j.Com4jObject> pStream,
    Holder<java.lang.String> pbstrMIMEType,
    Holder<Boolean> pfModified,
    Holder<java.lang.String> pbstrRedirectUrl);


  /**
   * @param bstrResourceUri Mandatory java.lang.String parameter.
   * @param pbstrLocalPath Mandatory Holder<java.lang.String> parameter.
   * @param pbstrMIMEType Mandatory Holder<java.lang.String> parameter.
   * @param pbstrRedirectUrl Mandatory Holder<java.lang.String> parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  void getLocalCopy(
    java.lang.String bstrResourceUri,
    Holder<java.lang.String> pbstrLocalPath,
    Holder<java.lang.String> pbstrMIMEType,
    Holder<java.lang.String> pbstrRedirectUrl);


  /**
   * @param pbstrLocalPath Mandatory java.lang.String parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  void releaseLocalCopy(
    java.lang.String pbstrLocalPath);


  // Properties:
}
