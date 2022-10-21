package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechResourceLoader Interface
 */
@IID("{B9AC5783-FCD0-4B21-B119-B4F8DA8FD2C3}")
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
