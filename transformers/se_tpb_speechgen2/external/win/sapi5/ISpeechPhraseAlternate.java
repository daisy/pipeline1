package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseAlternate Interface
 */
@IID("{27864A2A-2B9F-4CB8-92D3-0D2722FD1E73}")
public interface ISpeechPhraseAlternate extends Com4jObject {
    /**
     * RecoResult
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult recoResult();

    /**
     * StartElementInResult
     */
    @VTID(8)
    int startElementInResult();

    /**
     * NumberOfElementsInResult
     */
    @VTID(9)
    int numberOfElementsInResult();

    /**
     * Phrase
     */
    @VTID(10)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseInfo phraseInfo();

    /**
     * Commit
     */
    @VTID(11)
    void commit();

}
