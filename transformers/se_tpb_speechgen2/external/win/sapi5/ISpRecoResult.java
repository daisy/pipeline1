package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecoResult Interface
 */
@IID("{20B053BE-E235-43CD-9A2A-8D17A48B7842}")
public interface ISpRecoResult extends se_tpb_speechgen2.external.win.sapi5.ISpPhrase {
    @VTID(8)
    void getAlternates(
        int ulStartElement,
        int cElements,
        int ulRequestCount,
        Holder<se_tpb_speechgen2.external.win.sapi5.ISpPhraseAlt> ppPhrases,
        Holder<Integer> pcPhrasesReturned);

    @VTID(9)
    se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat getAudio(
        int ulStartElement,
        int cElements);

    @VTID(10)
    int speakAudio(
        int ulStartElement,
        int cElements,
        int dwFlags);

        @VTID(13)
        se_tpb_speechgen2.external.win.sapi5.ISpRecoContext getRecoContext();

    }
