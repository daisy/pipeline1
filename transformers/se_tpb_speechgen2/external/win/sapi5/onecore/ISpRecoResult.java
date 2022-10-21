package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpRecoResult Interface
 */
@IID("{20B053BE-E235-43CD-9A2A-8D17A48B7842}")
public interface ISpRecoResult extends se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhrase {
  // Methods:
  /**
   * @param ulStartElement Mandatory int parameter.
   * @param cElements Mandatory int parameter.
   * @param ulRequestCount Mandatory int parameter.
   * @param ppPhrases Mandatory Holder<se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhraseAlt> parameter.
   * @param pcPhrasesReturned Mandatory Holder<Integer> parameter.
   */

  @VTID(8)
  void getAlternates(
    int ulStartElement,
    int cElements,
    int ulRequestCount,
    Holder<se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhraseAlt> ppPhrases,
    Holder<Integer> pcPhrasesReturned);


  /**
   * @param ulStartElement Mandatory int parameter.
   * @param cElements Mandatory int parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.ISpStreamFormat
   */

  @VTID(9)
  se_tpb_speechgen2.external.win.sapi5.onecore.ISpStreamFormat getAudio(
    int ulStartElement,
    int cElements);


  /**
   * @param ulStartElement Mandatory int parameter.
   * @param cElements Mandatory int parameter.
   * @param dwFlags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @VTID(10)
  int speakAudio(
    int ulStartElement,
    int cElements,
    int dwFlags);


    /**
     * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoContext
     */

    @VTID(13)
    se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoContext getRecoContext();


    /**
     * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.ISpPredictorSet
     */

    @VTID(14)
    se_tpb_speechgen2.external.win.sapi5.onecore.ISpPredictorSet getPhrasePredictorSet();


    /**
     * @return  Returns a value of type GUID
     */

    @VTID(15)
    GUID getImpressionId();


    /**
     * @return  Returns a value of type java.lang.String
     */

    @VTID(16)
    @ReturnValue(type=NativeType.Unicode)
    java.lang.String getCUOutputJSONString();


    // Properties:
  }
