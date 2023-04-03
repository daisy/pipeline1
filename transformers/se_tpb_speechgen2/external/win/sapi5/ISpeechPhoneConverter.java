package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhoneConverter Interface
 */
@IID("{C3E4F353-433F-43D6-89A1-6A62A7054C3D}")
public interface ISpeechPhoneConverter extends Com4jObject {
  // Methods:
  /**
   * <p>
   * LanguageId
   * </p>
   * <p>
   * Getter method for the COM property "LanguageId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int languageId();


  /**
   * <p>
   * LanguageId
   * </p>
   * <p>
   * Setter method for the COM property "LanguageId"
   * </p>
   * @param languageId Mandatory int parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  void languageId(
    int languageId);


  /**
   * <p>
   * PhoneToId
   * </p>
   * @param phonemes Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object phoneToId(
    java.lang.String phonemes);


  /**
   * <p>
   * IdToPhone
   * </p>
   * @param idArray Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String idToPhone(
    @MarshalAs(NativeType.VARIANT) java.lang.Object idArray);


  // Properties:
}
