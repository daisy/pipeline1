package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseAlternate Interface
 */
@IID("{27864A2A-2B9F-4CB8-92D3-0D2722FD1E73}")
public interface ISpeechPhraseAlternate extends Com4jObject {
  // Methods:
  /**
   * <p>
   * RecoResult
   * </p>
   * <p>
   * Getter method for the COM property "RecoResult"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult recoResult();


  /**
   * <p>
   * StartElementInResult
   * </p>
   * <p>
   * Getter method for the COM property "StartElementInResult"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  int startElementInResult();


  /**
   * <p>
   * NumberOfElementsInResult
   * </p>
   * <p>
   * Getter method for the COM property "NumberOfElementsInResult"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  int numberOfElementsInResult();


  /**
   * <p>
   * Phrase
   * </p>
   * <p>
   * Getter method for the COM property "PhraseInfo"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseInfo
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseInfo phraseInfo();


  /**
   * <p>
   * Commit
   * </p>
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  void commit();


  // Properties:
}
