package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpResourceManager Interface
 */
@IID("{93384E18-5014-43D5-ADBB-A78E055926BD}")
public interface ISpResourceManager extends se_tpb_speechgen2.external.win.sapi5.onecore.IServiceProvider {
  // Methods:
  /**
   * @param guidServiceId Mandatory GUID parameter.
   * @param punkObject Mandatory com4j.Com4jObject parameter.
   */

  @VTID(4)
  void setObject(
    GUID guidServiceId,
    com4j.Com4jObject punkObject);


  /**
   * @param guidServiceId Mandatory GUID parameter.
   * @param objectCLSID Mandatory GUID parameter.
   * @param objectIID Mandatory GUID parameter.
   * @param fReleaseWhenLastExternalRefReleased Mandatory int parameter.
   * @return  Returns a value of type java.nio.Buffer
   */

  @VTID(5)
  java.nio.Buffer getObject(
    GUID guidServiceId,
    GUID objectCLSID,
    GUID objectIID,
    int fReleaseWhenLastExternalRefReleased);


  // Properties:
}
