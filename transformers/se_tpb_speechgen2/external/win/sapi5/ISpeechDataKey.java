package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechDataKey Interface
 */
@IID("{CE17C09B-4EFA-44D5-A4C9-59D9585AB0CD}")
public interface ISpeechDataKey extends Com4jObject {
  // Methods:
  /**
   * <p>
   * SetBinaryValue
   * </p>
   * @param valueName Mandatory java.lang.String parameter.
   * @param value Mandatory java.lang.Object parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  void setBinaryValue(
    java.lang.String valueName,
    @MarshalAs(NativeType.VARIANT) java.lang.Object value);


  /**
   * <p>
   * GetBinaryValue
   * </p>
   * @param valueName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getBinaryValue(
    java.lang.String valueName);


  /**
   * <p>
   * SetStringValue
   * </p>
   * @param valueName Mandatory java.lang.String parameter.
   * @param value Mandatory java.lang.String parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  void setStringValue(
    java.lang.String valueName,
    java.lang.String value);


  /**
   * <p>
   * GetStringValue
   * </p>
   * @param valueName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String getStringValue(
    java.lang.String valueName);


  /**
   * <p>
   * SetLongValue
   * </p>
   * @param valueName Mandatory java.lang.String parameter.
   * @param value Mandatory int parameter.
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  void setLongValue(
    java.lang.String valueName,
    int value);


  /**
   * <p>
   * GetlongValue
   * </p>
   * @param valueName Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  int getLongValue(
    java.lang.String valueName);


  /**
   * <p>
   * OpenKey
   * </p>
   * @param subKeyName Mandatory java.lang.String parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey openKey(
    java.lang.String subKeyName);


  /**
   * <p>
   * CreateKey
   * </p>
   * @param subKeyName Mandatory java.lang.String parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  se_tpb_speechgen2.external.win.sapi5.ISpeechDataKey createKey(
    java.lang.String subKeyName);


  /**
   * <p>
   * DeleteKey
   * </p>
   * @param subKeyName Mandatory java.lang.String parameter.
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  void deleteKey(
    java.lang.String subKeyName);


  /**
   * <p>
   * DeleteValue
   * </p>
   * @param valueName Mandatory java.lang.String parameter.
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(16)
  void deleteValue(
    java.lang.String valueName);


  /**
   * <p>
   * EnumKeys
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(17)
  java.lang.String enumKeys(
    int index);


  /**
   * <p>
   * EnumValues
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(18)
  java.lang.String enumValues(
    int index);


  // Properties:
}
