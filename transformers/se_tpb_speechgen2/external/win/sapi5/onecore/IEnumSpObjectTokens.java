package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * IEnumSpObjectTokens Interface
 */
@IID("{06B64F9E-7FDA-11D2-B4F2-00C04F797396}")
public interface IEnumSpObjectTokens extends Com4jObject {
  // Methods:
  /**
   * @param celt Mandatory int parameter.
   * @param pelt Mandatory Holder<se_tpb_speechgen2.external.win.sapi5.onecore.ISpObjectToken> parameter.
   * @param pceltFetched Mandatory Holder<Integer> parameter.
   */

  @VTID(3)
  void next(
    int celt,
    Holder<se_tpb_speechgen2.external.win.sapi5.onecore.ISpObjectToken> pelt,
    Holder<Integer> pceltFetched);


  /**
   * @param celt Mandatory int parameter.
   */

  @VTID(4)
  void skip(
    int celt);


  /**
   */

  @VTID(5)
  void reset();


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.IEnumSpObjectTokens
   */

  @VTID(6)
  se_tpb_speechgen2.external.win.sapi5.onecore.IEnumSpObjectTokens clone();


  /**
   * @param index Mandatory int parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.ISpObjectToken
   */

  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.onecore.ISpObjectToken item(
    int index);


  /**
   * @return  Returns a value of type int
   */

  @VTID(8)
  int getCount();


  // Properties:
}
