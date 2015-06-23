package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpObjectTokenCategory
 */
@IID("{2D3D3845-39AF-4850-BBF9-40B49780011D}")
public interface ISpObjectTokenCategory extends se_tpb_speechgen2.external.win.sapi5.ISpDataKey {
    @VTID(15)
    void setId(
        @MarshalAs(NativeType.Unicode) java.lang.String pszCategoryId,
        int fCreateIfNotExist);

    @VTID(16)
    Holder<Short> getId();

    @VTID(17)
    void getDataKey(
        se_tpb_speechgen2.external.win.sapi5.SPDATAKEYLOCATION spdkl,
        Holder<se_tpb_speechgen2.external.win.sapi5.ISpDataKey> ppDataKey);

    @VTID(18)
    se_tpb_speechgen2.external.win.sapi5.IEnumSpObjectTokens enumTokens(
        @MarshalAs(NativeType.Unicode) java.lang.String pzsReqAttribs,
        @MarshalAs(NativeType.Unicode) java.lang.String pszOptAttribs);

    @VTID(19)
    void setDefaultTokenId(
        @MarshalAs(NativeType.Unicode) java.lang.String pszTokenId);

    @VTID(20)
    Holder<Short> getDefaultTokenId();

}
