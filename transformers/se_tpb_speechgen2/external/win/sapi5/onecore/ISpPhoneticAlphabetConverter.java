package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpPhoneticAlphabetConverter Interface
 */
@IID("{133ADCD4-19B4-4020-9FDC-842E78253B17}")
public interface ISpPhoneticAlphabetConverter extends Com4jObject {
  // Methods:
  /**
   * @return  Returns a value of type short
   */

  @VTID(3)
  short getLangId();


  /**
   * @param langID Mandatory short parameter.
   */

  @VTID(4)
  void setLangId(
    short langID);


  /**
   * @param pszSAPIId Mandatory java.lang.String parameter.
   * @param cMaxLength Mandatory int parameter.
   * @return  Returns a value of type short
   */

  @VTID(5)
  @ReturnValue(index=1)
  short sapI2UPS(
    @MarshalAs(NativeType.Unicode) java.lang.String pszSAPIId,
    int cMaxLength);


  /**
   * @param pszUPSId Mandatory java.lang.String parameter.
   * @param cMaxLength Mandatory int parameter.
   * @return  Returns a value of type short
   */

  @VTID(6)
  @ReturnValue(index=1)
  short upS2SAPI(
    @MarshalAs(NativeType.Unicode) java.lang.String pszUPSId,
    int cMaxLength);


  /**
   * @param cSrcLength Mandatory int parameter.
   * @param bSAPI2UPS Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @VTID(7)
  int getMaxConvertLength(
    int cSrcLength,
    int bSAPI2UPS);


  // Properties:
}
