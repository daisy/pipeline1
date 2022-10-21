package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * IInternetSecurityManager Interface
 */
@IID("{79EAC9EE-BAF9-11CE-8C82-00AA004BA90B}")
public interface IInternetSecurityManager extends Com4jObject {
  // Methods:
  /**
   * @param pSite Mandatory se_tpb_speechgen2.external.win.sapi5.IInternetSecurityMgrSite parameter.
   */

  @VTID(3)
  void setSecuritySite(
    se_tpb_speechgen2.external.win.sapi5.IInternetSecurityMgrSite pSite);


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.IInternetSecurityMgrSite
   */

  @VTID(4)
  se_tpb_speechgen2.external.win.sapi5.IInternetSecurityMgrSite getSecuritySite();


  /**
   * @param pwszUrl Mandatory java.lang.String parameter.
   * @param dwFlags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @VTID(5)
  @ReturnValue(index=1)
  int mapUrlToZone(
    @MarshalAs(NativeType.Unicode) java.lang.String pwszUrl,
    int dwFlags);


  /**
   * @param pwszUrl Mandatory java.lang.String parameter.
   * @param pcbSecurityId Mandatory Holder<Integer> parameter.
   * @param dwReserved Mandatory long parameter.
   * @return  Returns a value of type byte
   */

  @VTID(6)
  @ReturnValue(index=1)
  byte getSecurityId(
    @MarshalAs(NativeType.Unicode) java.lang.String pwszUrl,
    Holder<Integer> pcbSecurityId,
    long dwReserved);


  /**
   * @param pwszUrl Mandatory java.lang.String parameter.
   * @param dwAction Mandatory int parameter.
   * @param cbPolicy Mandatory int parameter.
   * @param pContext Mandatory Holder<Byte> parameter.
   * @param cbContext Mandatory int parameter.
   * @param dwFlags Mandatory int parameter.
   * @param dwReserved Mandatory int parameter.
   * @return  Returns a value of type byte
   */

  @VTID(7)
  @ReturnValue(index=2)
  byte processUrlAction(
    @MarshalAs(NativeType.Unicode) java.lang.String pwszUrl,
    int dwAction,
    int cbPolicy,
    Holder<Byte> pContext,
    int cbContext,
    int dwFlags,
    int dwReserved);


    /**
     * @param dwZone Mandatory int parameter.
     * @param lpszPattern Mandatory java.lang.String parameter.
     * @param dwFlags Mandatory int parameter.
     */

    @VTID(9)
    void setZoneMapping(
      int dwZone,
      @MarshalAs(NativeType.Unicode) java.lang.String lpszPattern,
      int dwFlags);


    /**
     * @param dwZone Mandatory int parameter.
     * @param dwFlags Mandatory int parameter.
     * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.IEnumString
     */

    @VTID(10)
    @ReturnValue(index=1)
    se_tpb_speechgen2.external.win.sapi5.IEnumString getZoneMappings(
      int dwZone,
      int dwFlags);


    // Properties:
  }
