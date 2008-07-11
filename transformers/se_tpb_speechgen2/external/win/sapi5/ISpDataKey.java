package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpDataKey Interface
 */
@IID("{14056581-E16C-11D2-BB90-00C04F8EE6C0}")
public interface ISpDataKey extends Com4jObject {
    @VTID(3)
    void setData(
        @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
        int cbData,
        Holder<Byte> pData);

    @VTID(4)
    void getData(
        @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
        Holder<Integer> pcbData,
        Holder<Byte> pData);

    @VTID(5)
    void setStringValue(
        @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
        @MarshalAs(NativeType.Unicode) java.lang.String pszValue);

        @VTID(7)
        void setDWORD(
            @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
            int dwValue);

        @VTID(8)
        void getDWORD(
            @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
            Holder<Integer> pdwValue);

        @VTID(9)
        void openKey(
            @MarshalAs(NativeType.Unicode) java.lang.String pszSubKeyName,
            Holder<se_tpb_speechgen2.external.win.sapi5.ISpDataKey> ppSubKey);

        @VTID(10)
        void createKey(
            @MarshalAs(NativeType.Unicode) java.lang.String pszSubKey,
            Holder<se_tpb_speechgen2.external.win.sapi5.ISpDataKey> ppSubKey);

        @VTID(11)
        void deleteKey(
            @MarshalAs(NativeType.Unicode) java.lang.String pszSubKey);

        @VTID(12)
        void deleteValue(
            @MarshalAs(NativeType.Unicode) java.lang.String pszValueName);

            }
