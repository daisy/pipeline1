package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpObjectWithToken Interface
 */
@IID("{5B559F40-E952-11D2-BB91-00C04F8EE6C0}")
public interface ISpObjectWithToken extends Com4jObject {
  // Methods:
  /**
   * @param pToken Mandatory se_tpb_speechgen2.external.win.sapi5.ISpObjectToken parameter.
   */

  @VTID(3)
  void setObjectToken(
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken pToken);


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpObjectToken
   */

  @VTID(4)
  se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getObjectToken();


  // Properties:
}
