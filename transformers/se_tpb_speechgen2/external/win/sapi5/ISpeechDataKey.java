package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechDataKey Interface
 */
@IID("{CE17C09B-4EFA-44D5-A4C9-59D9585AB0CD}")
public interface ISpeechDataKey extends Com4jObject {
    /**
     * SetBinaryValue
     */
    @VTID(7)
    void setBinaryValue(
        java.lang.String valueName,
        @MarshalAs(NativeType.VARIANT) java.lang.Object value);

    /**
     * GetBinaryValue
     */
    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object getBinaryValue(
        java.lang.String valueName);

    /**
     * SetStringValue
     */
    @VTID(9)
    void setStringValue(
        java.lang.String valueName,
        java.lang.String value);

    /**
     * GetStringValue
     */
    @VTID(10)
    java.lang.String getStringValue(
        java.lang.String valueName);

    /**
     * SetLongValue
     */
    @VTID(11)
    void setLongValue(
        java.lang.String valueName,
        int value);

    /**
     * GetlongValue
     */
    @VTID(12)
    int getLongValue(
        java.lang.String valueName);

    /**
     * OpenKey
     */
    @VTID(13)
    se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey openKey(
        java.lang.String subKeyName);

    /**
     * CreateKey
     */
    @VTID(14)
    se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey createKey(
        java.lang.String subKeyName);

    /**
     * DeleteKey
     */
    @VTID(15)
    void deleteKey(
        java.lang.String subKeyName);

    /**
     * DeleteValue
     */
    @VTID(16)
    void deleteValue(
        java.lang.String valueName);

    /**
     * EnumKeys
     */
    @VTID(17)
    java.lang.String enumKeys(
        int index);

    /**
     * EnumValues
     */
    @VTID(18)
    java.lang.String enumValues(
        int index);

}
