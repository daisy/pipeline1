package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpRecoResult Interface
 */
@IID("{20B053BE-E235-43CD-9A2A-8D17A48B7842}")
public interface ISpRecoResult extends org.daisy.util.wincom.sapi5.ISpPhrase {
    @VTID(8)
    void getAlternates(
        int ulStartElement,
        int cElements,
        int ulRequestCount,
        Holder<org.daisy.util.wincom.sapi5.ISpPhraseAlt> ppPhrases,
        Holder<Integer> pcPhrasesReturned);

    @VTID(9)
    org.daisy.util.wincom.sapi5.ISpStreamFormat getAudio(
        int ulStartElement,
        int cElements);

    @VTID(10)
    int speakAudio(
        int ulStartElement,
        int cElements,
        int dwFlags);

        @VTID(13)
        org.daisy.util.wincom.sapi5.ISpRecoContext getRecoContext();

    }
