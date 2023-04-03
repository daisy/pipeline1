package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechObjectTokenCategory Interface
 */
@IID("{CA7EAC50-2D01-4145-86D4-5AE7D70F4469}")
public interface ISpeechObjectTokenCategory extends Com4jObject {
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
   * Default
   * </p>
   * <p>
   * Setter method for the COM property "Default"
   * </p>
   * @param tokenId Mandatory java.lang.String parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  void _default(
    java.lang.String tokenId);


  /**
   * <p>
   * Default
   * </p>
   * <p>
   * Getter method for the COM property "Default"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String _default();


  /**
   * <p>
   * SetId
   * </p>
   * @param id Mandatory java.lang.String parameter.
   * @param createIfNotExist Optional parameter. Default value is false
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(10)
  void setId(
    java.lang.String id,
    @Optional @DefaultValue("0") boolean createIfNotExist);


  /**
   * <p>
   * GetDataKey
   * </p>
   * @param location Optional parameter. Default value is 0
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(11)
  se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey getDataKey(
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechDataKeyLocation location);


  /**
   * <p>
   * EnumerateTokens
   * </p>
   * @param requiredAttributes Optional parameter. Default value is ""
   * @param optionalAttributes Optional parameter. Default value is ""
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokens
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(12)
  se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokens enumerateTokens(
    @Optional @DefaultValue("") java.lang.String requiredAttributes,
    @Optional @DefaultValue("") java.lang.String optionalAttributes);


  // Properties:
}
