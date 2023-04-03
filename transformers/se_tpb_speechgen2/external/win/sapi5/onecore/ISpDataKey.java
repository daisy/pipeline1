package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpDataKey Interface
 */
@IID("{14056581-E16C-11D2-BB90-00C04F8EE6C0}")
public interface ISpDataKey extends Com4jObject {
  // Methods:
  /**
   * @param pszValueName Mandatory java.lang.String parameter.
   * @param cbData Mandatory int parameter.
   * @param pData Mandatory Holder&lt;Byte&gt; parameter.
   */

  @VTID(3)
  void setData(
    @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
    int cbData,
    Holder<Byte> pData);


  /**
   * @param pszValueName Mandatory java.lang.String parameter.
   * @param pcbData Mandatory Holder&lt;Integer&gt; parameter.
   * @return  Returns a value of type byte
   */

  @VTID(4)
  byte getData(
    @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
    Holder<Integer> pcbData);


  /**
   * @param pszValueName Mandatory java.lang.String parameter.
   * @param pszValue Mandatory java.lang.String parameter.
   */

  @VTID(5)
  void setStringValue(
    @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
    @MarshalAs(NativeType.Unicode) java.lang.String pszValue);


  /**
   * @param pszValueName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @VTID(6)
  @ReturnValue(type=NativeType.Unicode)
  java.lang.String getStringValue(
    @MarshalAs(NativeType.Unicode) java.lang.String pszValueName);


  /**
   * @param pszValueName Mandatory java.lang.String parameter.
   * @param dwValue Mandatory int parameter.
   */

  @VTID(7)
  void setDWORD(
    @MarshalAs(NativeType.Unicode) java.lang.String pszValueName,
    int dwValue);


  /**
   * @param pszValueName Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @VTID(8)
  int getDWORD(
    @MarshalAs(NativeType.Unicode) java.lang.String pszValueName);


  /**
   * @param pszSubKeyName Mandatory java.lang.String parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.ISpDataKey
   */

  @VTID(9)
  se_tpb_speechgen2.external.win.sapi5.onecore.ISpDataKey openKey(
    @MarshalAs(NativeType.Unicode) java.lang.String pszSubKeyName);


  /**
   * @param pszSubKey Mandatory java.lang.String parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.ISpDataKey
   */

  @VTID(10)
  se_tpb_speechgen2.external.win.sapi5.onecore.ISpDataKey createKey(
    @MarshalAs(NativeType.Unicode) java.lang.String pszSubKey);


  /**
   * @param pszSubKey Mandatory java.lang.String parameter.
   */

  @VTID(11)
  void deleteKey(
    @MarshalAs(NativeType.Unicode) java.lang.String pszSubKey);


  /**
   * @param pszValueName Mandatory java.lang.String parameter.
   */

  @VTID(12)
  void deleteValue(
    @MarshalAs(NativeType.Unicode) java.lang.String pszValueName);


  /**
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @VTID(13)
  @ReturnValue(type=NativeType.Unicode)
  java.lang.String enumKeys(
    int index);


  /**
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @VTID(14)
  @ReturnValue(type=NativeType.Unicode)
  java.lang.String enumValues(
    int index);


  // Properties:
}
