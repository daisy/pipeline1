package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechObjectToken Interface
 */
@IID("{C74A3ADC-B727-4500-A84A-B526721C8B8C}")
public interface ISpeechObjectToken extends Com4jObject {
    /**
     * Id
     */
    @VTID(7)
    java.lang.String id();

    /**
     * DataKey
     */
    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey dataKey();

    /**
     * Category
     */
    @VTID(9)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokenCategory category();

    /**
     * GetDescription
     */
    @VTID(10)
    java.lang.String getDescription(
        @DefaultValue("0")int locale);

    /**
     * SetId
     */
    @VTID(11)
    void setId(
        java.lang.String id,
        @DefaultValue("")java.lang.String categoryID,
        @DefaultValue("0")boolean createIfNotExist);

    /**
     * GetAttribute
     */
    @VTID(12)
    java.lang.String getAttribute(
        java.lang.String attributeName);

    /**
     * CreateInstance
     */
    @VTID(13)
    com4j.Com4jObject createInstance(
        com4j.Com4jObject pUnkOuter,
        @DefaultValue("23")se_tpb_speechgen2.external.win.sapi5.SpeechTokenContext clsContext);

    /**
     * Remove
     */
    @VTID(14)
    void remove(
        java.lang.String objectStorageCLSID);

    /**
     * GetStorageFileName
     */
    @VTID(15)
    java.lang.String getStorageFileName(
        java.lang.String objectStorageCLSID,
        java.lang.String keyName,
        java.lang.String fileName,
        se_tpb_speechgen2.external.win.sapi5.SpeechTokenShellFolder folder);

    /**
     * RemoveStorageFileName
     */
    @VTID(16)
    void removeStorageFileName(
        java.lang.String objectStorageCLSID,
        java.lang.String keyName,
        boolean deleteFile);

    /**
     * IsUISupported
     */
    @VTID(17)
    boolean isUISupported(
        java.lang.String typeOfUI,
        @DefaultValue("")java.lang.Object extraData,
        com4j.Com4jObject object);

    /**
     * DisplayUI
     */
    @VTID(18)
    void displayUI(
        int hWnd,
        java.lang.String title,
        java.lang.String typeOfUI,
        @DefaultValue("")java.lang.Object extraData,
        com4j.Com4jObject object);

    /**
     * MatchesAttributes
     */
    @VTID(19)
    boolean matchesAttributes(
        java.lang.String attributes);

}
