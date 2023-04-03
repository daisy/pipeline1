package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpObjectTokenCategory
 */
@IID("{2D3D3845-39AF-4850-BBF9-40B49780011D}")
public interface ISpObjectTokenCategory extends se_tpb_speechgen2.external.win.sapi5.ISpDataKey {
  // Methods:
  /**
   * @param pszCategoryId Mandatory java.lang.String parameter.
   * @param fCreateIfNotExist Mandatory int parameter.
   */

  @VTID(15)
  void setId(
    @MarshalAs(NativeType.Unicode) java.lang.String pszCategoryId,
    int fCreateIfNotExist);


  /**
   * @return  Returns a value of type java.lang.String
   */

  @VTID(16)
  @ReturnValue(type=NativeType.Unicode)
  java.lang.String getId();


  /**
   * @param spdkl Mandatory se_tpb_speechgen2.external.win.sapi5.SPDATAKEYLOCATION parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpDataKey
   */

  @VTID(17)
  se_tpb_speechgen2.external.win.sapi5.ISpDataKey getDataKey(
    se_tpb_speechgen2.external.win.sapi5.SPDATAKEYLOCATION spdkl);


  /**
   * @param pzsReqAttribs Mandatory java.lang.String parameter.
   * @param pszOptAttribs Mandatory java.lang.String parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.IEnumSpObjectTokens
   */

  @VTID(18)
  se_tpb_speechgen2.external.win.sapi5.IEnumSpObjectTokens enumTokens(
    @MarshalAs(NativeType.Unicode) java.lang.String pzsReqAttribs,
    @MarshalAs(NativeType.Unicode) java.lang.String pszOptAttribs);


  /**
   * @param pszTokenId Mandatory java.lang.String parameter.
   */

  @VTID(19)
  void setDefaultTokenId(
    @MarshalAs(NativeType.Unicode) java.lang.String pszTokenId);


  /**
   * @return  Returns a value of type java.lang.String
   */

  @VTID(20)
  @ReturnValue(type=NativeType.Unicode)
  java.lang.String getDefaultTokenId();


  // Properties:
}
