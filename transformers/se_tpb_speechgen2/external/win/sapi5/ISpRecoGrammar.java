package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecoGrammar Interface
 */
@IID("{2177DB29-7F45-47D0-8554-067E91C80502}")
public interface ISpRecoGrammar extends se_tpb_speechgen2.external.win.sapi5.ISpGrammarBuilder {
    @VTID(11)
    long getGrammarId();

    @VTID(12)
    se_tpb_speechgen2.external.win.sapi5.ISpRecoContext getRecoContext();

    @VTID(13)
    void loadCmdFromFile(
        @MarshalAs(NativeType.Unicode) java.lang.String pszFileName,
        se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);

    @VTID(14)
    void loadCmdFromObject(
        GUID rcid,
        @MarshalAs(NativeType.Unicode) java.lang.String pszGrammarName,
        se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);

    @VTID(15)
    void loadCmdFromResource(
        java.nio.Buffer hModule,
        @MarshalAs(NativeType.Unicode) java.lang.String pszResourceName,
        @MarshalAs(NativeType.Unicode) java.lang.String pszResourceType,
        short wLanguage,
        se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);

        @VTID(17)
        void loadCmdFromProprietaryGrammar(
            GUID rguidParam,
            @MarshalAs(NativeType.Unicode) java.lang.String pszStringParam,
            java.nio.Buffer pvDataPrarm,
            int cbDataSize,
            se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);

        @VTID(18)
        void setRuleState(
            @MarshalAs(NativeType.Unicode) java.lang.String pszName,
            java.nio.Buffer pReserved,
            se_tpb_speechgen2.external.win.sapi5.SPRULESTATE newState);

        @VTID(19)
        void setRuleIdState(
            int ulRuleId,
            se_tpb_speechgen2.external.win.sapi5.SPRULESTATE newState);

        @VTID(20)
        void loadDictation(
            @MarshalAs(NativeType.Unicode) java.lang.String pszTopicName,
            se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);

        @VTID(21)
        void unloadDictation();

        @VTID(22)
        void setDictationState(
            se_tpb_speechgen2.external.win.sapi5.SPRULESTATE newState);

                @VTID(25)
                se_tpb_speechgen2.external.win.sapi5.SPWORDPRONOUNCEABLE isPronounceable(
                    @MarshalAs(NativeType.Unicode) java.lang.String pszWord);

                @VTID(26)
                void setGrammarState(
                    se_tpb_speechgen2.external.win.sapi5.SPGRAMMARSTATE eGrammarState);

                @VTID(27)
                Holder<Short> saveCmd(
                    se_tpb_speechgen2.external.win.sapi5.IStream pStream);

                @VTID(28)
                se_tpb_speechgen2.external.win.sapi5.SPGRAMMARSTATE getGrammarState();

            }
