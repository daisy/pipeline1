package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechObjectToken Interface
 */
@IID("{C74A3ADC-B727-4500-A84A-B526721C8B8C}")
public interface ISpeechObjectToken extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Id
   * </p>
   * <p>
   * Getter method for the COM property "Id"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String id();


  /**
   * <p>
   * DataKey
   * </p>
   * <p>
   * Getter method for the COM property "DataKey"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey dataKey();


  /**
   * <p>
   * Category
   * </p>
   * <p>
   * Getter method for the COM property "Category"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokenCategory
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokenCategory category();


  /**
   * <p>
   * GetDescription
   * </p>
   * @param locale Optional parameter. Default value is 0
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String getDescription(
    @Optional @DefaultValue("0") int locale);


  /**
   * <p>
   * SetId
   * </p>
   * @param id Mandatory java.lang.String parameter.
   * @param categoryID Optional parameter. Default value is ""
   * @param createIfNotExist Optional parameter. Default value is false
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  void setId(
    java.lang.String id,
    @Optional @DefaultValue("") java.lang.String categoryID,
    @Optional @DefaultValue("0") boolean createIfNotExist);


  /**
   * <p>
   * GetAttribute
   * </p>
   * @param attributeName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  java.lang.String getAttribute(
    java.lang.String attributeName);


  /**
   * <p>
   * CreateInstance
   * </p>
   * @param pUnkOuter Optional parameter. Default value is unprintable.
   * @param clsContext Optional parameter. Default value is 23
   * @return  Returns a value of type com4j.Com4jObject
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  com4j.Com4jObject createInstance(
    @Optional com4j.Com4jObject pUnkOuter,
    @Optional @DefaultValue("23") se_tpb_speechgen2.external.win.sapi5.SpeechTokenContext clsContext);


  /**
   * <p>
   * Remove
   * </p>
   * @param objectStorageCLSID Mandatory java.lang.String parameter.
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  void remove(
    java.lang.String objectStorageCLSID);


  /**
   * <p>
   * GetStorageFileName
   * </p>
   * @param objectStorageCLSID Mandatory java.lang.String parameter.
   * @param keyName Mandatory java.lang.String parameter.
   * @param fileName Mandatory java.lang.String parameter.
   * @param folder Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechTokenShellFolder parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  java.lang.String getStorageFileName(
    java.lang.String objectStorageCLSID,
    java.lang.String keyName,
    java.lang.String fileName,
    se_tpb_speechgen2.external.win.sapi5.SpeechTokenShellFolder folder);


  /**
   * <p>
   * RemoveStorageFileName
   * </p>
   * @param objectStorageCLSID Mandatory java.lang.String parameter.
   * @param keyName Mandatory java.lang.String parameter.
   * @param deleteFile Mandatory boolean parameter.
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(16)
  void removeStorageFileName(
    java.lang.String objectStorageCLSID,
    java.lang.String keyName,
    boolean deleteFile);


  /**
   * <p>
   * IsUISupported
   * </p>
   * @param typeOfUI Mandatory java.lang.String parameter.
   * @param extraData Optional parameter. Default value is ""
   * @param object Optional parameter. Default value is unprintable.
   * @return  Returns a value of type boolean
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(17)
  boolean isUISupported(
    java.lang.String typeOfUI,
    @Optional @DefaultValue("") java.lang.Object extraData,
    @Optional com4j.Com4jObject object);


  /**
   * <p>
   * DisplayUI
   * </p>
   * @param hWnd Mandatory int parameter.
   * @param title Mandatory java.lang.String parameter.
   * @param typeOfUI Mandatory java.lang.String parameter.
   * @param extraData Optional parameter. Default value is ""
   * @param object Optional parameter. Default value is unprintable.
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(18)
  void displayUI(
    int hWnd,
    java.lang.String title,
    java.lang.String typeOfUI,
    @Optional @DefaultValue("") java.lang.Object extraData,
    @Optional com4j.Com4jObject object);


  /**
   * <p>
   * MatchesAttributes
   * </p>
   * @param attributes Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(13) //= 0xd. The runtime will prefer the VTID if present
  @VTID(19)
  boolean matchesAttributes(
    java.lang.String attributes);


  // Properties:
}
