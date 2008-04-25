package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseInfoBuilder Interface
 */
@IID("{3B151836-DF3A-4E0A-846C-D2ADC9334333}")
public interface ISpeechPhraseInfoBuilder extends Com4jObject {
    /**
     * RestorePhraseFromMemory
     */
    @VTID(7)
    org.daisy.util.wincom.sapi5.ISpeechPhraseInfo restorePhraseFromMemory(
        java.lang.Object phraseInMemory);

}
