package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpRecoContext Interface
 */
@IID("{F740A62F-7C15-489E-8234-940A33D9272D}")
public interface ISpRecoContext extends org.daisy.util.wincom.sapi5.ISpEventSource {
    @VTID(13)
    org.daisy.util.wincom.sapi5.ISpRecognizer getRecognizer();

    @VTID(14)
    org.daisy.util.wincom.sapi5.ISpRecoGrammar createGrammar(
        long ullGrammarID);

    @VTID(16)
    void getMaxAlternates(
        Holder<Integer> pcAlternates);

    @VTID(17)
    void setMaxAlternates(
        int cAlternates);

                @VTID(21)
                void bookmark(
                    org.daisy.util.wincom.sapi5.SPBOOKMARKOPTIONS options,
                    long ullStreamPosition,
                    int lparamEvent);

                @VTID(22)
                void setAdaptationData(
                    @MarshalAs(NativeType.Unicode) java.lang.String pAdaptationData,
                    int cch);

                @VTID(23)
                void pause(
                    int dwReserved);

                @VTID(24)
                void resume(
                    int dwReserved);

                @VTID(25)
                void setVoice(
                    org.daisy.util.wincom.sapi5.ISpVoice pVoice,
                    int fAllowFormatChanges);

                @VTID(26)
                org.daisy.util.wincom.sapi5.ISpVoice getVoice();

                @VTID(27)
                void setVoicePurgeEvent(
                    long ullEventInterest);

                @VTID(28)
                long getVoicePurgeEvent();

                @VTID(29)
                void setContextState(
                    org.daisy.util.wincom.sapi5.SPCONTEXTSTATE eContextState);

                @VTID(30)
                void getContextState(
                    Holder<org.daisy.util.wincom.sapi5.SPCONTEXTSTATE> peContextState);

            }
