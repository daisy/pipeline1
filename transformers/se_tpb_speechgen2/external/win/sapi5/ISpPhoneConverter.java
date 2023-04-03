package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpPhoneConverter Interface
 */
@IID("{8445C581-0CAC-4A38-ABFE-9B2CE2826455}")
public interface ISpPhoneConverter extends se_tpb_speechgen2.external.win.sapi5.ISpObjectWithToken {
  // Methods:
  /**
   * @param pszPhone Mandatory java.lang.String parameter.
   * @return  Returns a value of type short
   */

  @VTID(5)
  short phoneToId(
    @MarshalAs(NativeType.Unicode) java.lang.String pszPhone);


  /**
   * @param pId Mandatory java.lang.String parameter.
   * @return  Returns a value of type short
   */

  @VTID(6)
  short idToPhone(
    @MarshalAs(NativeType.Unicode) java.lang.String pId);


  // Properties:
}
