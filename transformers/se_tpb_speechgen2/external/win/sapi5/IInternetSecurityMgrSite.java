package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * IInternetSecurityMgrSite Interface
 */
@IID("{79EAC9ED-BAF9-11CE-8C82-00AA004BA90B}")
public interface IInternetSecurityMgrSite extends Com4jObject {
  // Methods:
  /**
   * @return  Returns a value of type int
   */

  @VTID(3)
  int getWindow();


  /**
   * @param fEnable Mandatory int parameter.
   */

  @VTID(4)
  void enableModeless(
    int fEnable);


  // Properties:
}
