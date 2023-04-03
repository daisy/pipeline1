package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpPhraseAlt Interface
 */
@IID("{8FCEBC98-4E49-4067-9C6C-D86A0E092E3D}")
public interface ISpPhraseAlt extends se_tpb_speechgen2.external.win.sapi5.ISpPhrase {
  // Methods:
  /**
   * @param ppParent Mandatory Holder&lt;se_tpb_speechgen2.external.win.sapi5.ISpPhrase&gt; parameter.
   * @param pulStartElementInParent Mandatory Holder&lt;Integer&gt; parameter.
   * @param pcElementsInParent Mandatory Holder&lt;Integer&gt; parameter.
   * @param pcElementsInAlt Mandatory Holder&lt;Integer&gt; parameter.
   */

  @VTID(7)
  void getAltInfo(
    Holder<se_tpb_speechgen2.external.win.sapi5.ISpPhrase> ppParent,
    Holder<Integer> pulStartElementInParent,
    Holder<Integer> pcElementsInParent,
    Holder<Integer> pcElementsInAlt);


  /**
   */

  @VTID(8)
  void commit();


  // Properties:
}
