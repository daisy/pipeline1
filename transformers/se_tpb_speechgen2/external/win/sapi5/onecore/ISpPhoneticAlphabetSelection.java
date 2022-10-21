package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpPhoneticAlphabetSelection Interface
 */
@IID("{B2745EFD-42CE-48CA-81F1-A96E02538A90}")
public interface ISpPhoneticAlphabetSelection extends Com4jObject {
  // Methods:
  /**
   * @return  Returns a value of type int
   */

  @VTID(3)
  int isAlphabetUPS();


  /**
   * @param fForceUPS Mandatory int parameter.
   */

  @VTID(4)
  void setAlphabetToUPS(
    int fForceUPS);


  // Properties:
}
