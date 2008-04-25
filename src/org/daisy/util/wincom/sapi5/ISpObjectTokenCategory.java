package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpObjectTokenCategory
 */
@IID("{2D3D3845-39AF-4850-BBF9-40B49780011D}")
public interface ISpObjectTokenCategory extends org.daisy.util.wincom.sapi5.ISpDataKey {
    @VTID(15)
    void setId(
        @MarshalAs(NativeType.Unicode) java.lang.String pszCategoryId,
        int fCreateIfNotExist);

    @VTID(16)
    Holder<Short> getId();

    @VTID(17)
    void getDataKey(
        org.daisy.util.wincom.sapi5.SPDATAKEYLOCATION spdkl,
        Holder<org.daisy.util.wincom.sapi5.ISpDataKey> ppDataKey);

    @VTID(18)
    org.daisy.util.wincom.sapi5.IEnumSpObjectTokens enumTokens(
        @MarshalAs(NativeType.Unicode) java.lang.String pzsReqAttribs,
        @MarshalAs(NativeType.Unicode) java.lang.String pszOptAttribs);

    @VTID(19)
    void setDefaultTokenId(
        @MarshalAs(NativeType.Unicode) java.lang.String pszTokenId);

    @VTID(20)
    Holder<Short> getDefaultTokenId();

}
