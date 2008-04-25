package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpObjectToken Interface
 */
@IID("{14056589-E16C-11D2-BB90-00C04F8EE6C0}")
public interface ISpObjectToken extends org.daisy.util.wincom.sapi5.ISpDataKey {
    @VTID(15)
    void setId(
        @MarshalAs(NativeType.Unicode) java.lang.String pszCategoryId,
        @MarshalAs(NativeType.Unicode) java.lang.String pszTokenId,
        int fCreateIfNotExist);

        @VTID(17)
        void getCategory(
            Holder<org.daisy.util.wincom.sapi5.ISpObjectTokenCategory> ppTokenCategory);

        @VTID(18)
        java.nio.Buffer createInstance(
            com4j.Com4jObject pUnkOuter,
            int dwClsContext,
            GUID riid);

        @VTID(19)
        Holder<Short> getStorageFileName(
            GUID clsidCaller,
            @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
            @MarshalAs(NativeType.Unicode) java.lang.String pszFileNameSpecifier,
            int nFolder);

        @VTID(20)
        void removeStorageFileName(
            GUID clsidCaller,
            @MarshalAs(NativeType.Unicode) java.lang.String pszKeyName,
            int fDeleteFile);

        @VTID(21)
        void remove(
            GUID pclsidCaller);

        @VTID(22)
        int isUISupported(
            @MarshalAs(NativeType.Unicode) java.lang.String pszTypeOfUI,
            java.nio.Buffer pvExtraData,
            int cbExtraData,
            com4j.Com4jObject punkObject);

        @VTID(23)
        void displayUI(
            int hWndParent,
            @MarshalAs(NativeType.Unicode) java.lang.String pszTitle,
            @MarshalAs(NativeType.Unicode) java.lang.String pszTypeOfUI,
            java.nio.Buffer pvExtraData,
            int cbExtraData,
            com4j.Com4jObject punkObject);

        @VTID(24)
        int matchesAttributes(
            @MarshalAs(NativeType.Unicode) java.lang.String pszAttributes);

    }
