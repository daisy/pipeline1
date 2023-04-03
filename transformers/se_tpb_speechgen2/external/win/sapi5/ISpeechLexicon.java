package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechLexicon Interface
 */
@IID("{3DA7627A-C7AE-4B23-8708-638C50362C25}")
public interface ISpeechLexicon extends Com4jObject {
  // Methods:
  /**
   * <p>
   * GenerationId
   * </p>
   * <p>
   * Getter method for the COM property "GenerationId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int generationId();


  /**
   * <p>
   * GetWords
   * </p>
   * @param flags Optional parameter. Default value is 3
   * @param generationId Optional parameter. Default value is 0
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconWords
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconWords getWords(
    @Optional @DefaultValue("3") se_tpb_speechgen2.external.win.sapi5.SpeechLexiconType flags,
    @Optional @DefaultValue("0") Holder<Integer> generationId);


  /**
   * <p>
   * AddPronunciation
   * </p>
   * @param bstrWord Mandatory java.lang.String parameter.
   * @param langId Mandatory int parameter.
   * @param partOfSpeech Optional parameter. Default value is 0
   * @param bstrPronunciation Optional parameter. Default value is ""
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  void addPronunciation(
    java.lang.String bstrWord,
    int langId,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechPartOfSpeech partOfSpeech,
    @Optional @DefaultValue("") java.lang.String bstrPronunciation);


  /**
   * <p>
   * AddPronunciationByPhoneIds
   * </p>
   * @param bstrWord Mandatory java.lang.String parameter.
   * @param langId Mandatory int parameter.
   * @param partOfSpeech Optional parameter. Default value is 0
   * @param phoneIds Optional parameter. Default value is ""
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  void addPronunciationByPhoneIds(
    java.lang.String bstrWord,
    int langId,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechPartOfSpeech partOfSpeech,
    @Optional @DefaultValue("") java.lang.Object phoneIds);


  /**
   * <p>
   * RemovePronunciation
   * </p>
   * @param bstrWord Mandatory java.lang.String parameter.
   * @param langId Mandatory int parameter.
   * @param partOfSpeech Optional parameter. Default value is 0
   * @param bstrPronunciation Optional parameter. Default value is ""
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  void removePronunciation(
    java.lang.String bstrWord,
    int langId,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechPartOfSpeech partOfSpeech,
    @Optional @DefaultValue("") java.lang.String bstrPronunciation);


  /**
   * <p>
   * RemovePronunciationByPhoneIds
   * </p>
   * @param bstrWord Mandatory java.lang.String parameter.
   * @param langId Mandatory int parameter.
   * @param partOfSpeech Optional parameter. Default value is 0
   * @param phoneIds Optional parameter. Default value is ""
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  void removePronunciationByPhoneIds(
    java.lang.String bstrWord,
    int langId,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechPartOfSpeech partOfSpeech,
    @Optional @DefaultValue("") java.lang.Object phoneIds);


  /**
   * <p>
   * GetPronunciations
   * </p>
   * @param bstrWord Mandatory java.lang.String parameter.
   * @param langId Optional parameter. Default value is 0
   * @param typeFlags Optional parameter. Default value is 3
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciations
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciations getPronunciations(
    java.lang.String bstrWord,
    @Optional @DefaultValue("0") int langId,
    @Optional @DefaultValue("3") se_tpb_speechgen2.external.win.sapi5.SpeechLexiconType typeFlags);


  /**
   * <p>
   * GetGenerationChange
   * </p>
   * @param generationId Mandatory Holder&lt;Integer&gt; parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconWords
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconWords getGenerationChange(
    Holder<Integer> generationId);


  // Properties:
}
