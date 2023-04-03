package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpProperties Interface
 */
@IID("{5B4FB971-B115-4DE1-AD97-E482E3BF6EE4}")
public interface ISpProperties extends Com4jObject {
  // Methods:
  /**
   * @param pName Mandatory java.lang.String parameter.
   * @param lValue Mandatory int parameter.
   */

  @VTID(3)
  void setPropertyNum(
    @MarshalAs(NativeType.Unicode) java.lang.String pName,
    int lValue);


  /**
   * @param pName Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @VTID(4)
  int getPropertyNum(
    @MarshalAs(NativeType.Unicode) java.lang.String pName);


  /**
   * @param pName Mandatory java.lang.String parameter.
   * @param pValue Mandatory java.lang.String parameter.
   */

  @VTID(5)
  void setPropertyString(
    @MarshalAs(NativeType.Unicode) java.lang.String pName,
    @MarshalAs(NativeType.Unicode) java.lang.String pValue);


  /**
   * @param pName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @VTID(6)
  @ReturnValue(type=NativeType.Unicode)
  java.lang.String getPropertyString(
    @MarshalAs(NativeType.Unicode) java.lang.String pName);


  // Properties:
}
