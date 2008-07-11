package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecoContext Interface
 */
@IID("{F740A62F-7C15-489E-8234-940A33D9272D}")
public interface ISpRecoContext extends se_tpb_speechgen2.external.win.sapi5.ISpEventSource {
    @VTID(13)
    se_tpb_speechgen2.external.win.sapi5.ISpRecognizer getRecognizer();

    @VTID(14)
    se_tpb_speechgen2.external.win.sapi5.ISpRecoGrammar createGrammar(
        long ullGrammarID);

    @VTID(16)
    void getMaxAlternates(
        Holder<Integer> pcAlternates);

    @VTID(17)
    void setMaxAlternates(
        int cAlternates);

                @VTID(21)
                void bookmark(
                    se_tpb_speechgen2.external.win.sapi5.SPBOOKMARKOPTIONS options,
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
                    se_tpb_speechgen2.external.win.sapi5.ISpVoice pVoice,
                    int fAllowFormatChanges);

                @VTID(26)
                se_tpb_speechgen2.external.win.sapi5.ISpVoice getVoice();

                @VTID(27)
                void setVoicePurgeEvent(
                    long ullEventInterest);

                @VTID(28)
                long getVoicePurgeEvent();

                @VTID(29)
                void setContextState(
                    se_tpb_speechgen2.external.win.sapi5.SPCONTEXTSTATE eContextState);

                @VTID(30)
                void getContextState(
                    Holder<se_tpb_speechgen2.external.win.sapi5.SPCONTEXTSTATE> peContextState);

            }
