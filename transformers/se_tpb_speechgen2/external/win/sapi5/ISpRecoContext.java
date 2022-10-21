package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecoContext Interface
 */
@IID("{F740A62F-7C15-489E-8234-940A33D9272D}")
public interface ISpRecoContext extends se_tpb_speechgen2.external.win.sapi5.ISpEventSource {
  // Methods:
  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpRecognizer
   */

  @VTID(13)
  se_tpb_speechgen2.external.win.sapi5.ISpRecognizer getRecognizer();


  /**
   * @param ullGrammarID Mandatory long parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpRecoGrammar
   */

  @VTID(14)
  se_tpb_speechgen2.external.win.sapi5.ISpRecoGrammar createGrammar(
    long ullGrammarID);


  /**
   * @param pcAlternates Mandatory Holder<Integer> parameter.
   */

  @VTID(16)
  void getMaxAlternates(
    Holder<Integer> pcAlternates);


  /**
   * @param cAlternates Mandatory int parameter.
   */

  @VTID(17)
  void setMaxAlternates(
    int cAlternates);


        /**
         * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPBOOKMARKOPTIONS parameter.
         * @param ullStreamPosition Mandatory long parameter.
         * @param lparamEvent Mandatory long parameter.
         */

        @VTID(21)
        void bookmark(
          se_tpb_speechgen2.external.win.sapi5.SPBOOKMARKOPTIONS options,
          long ullStreamPosition,
          long lparamEvent);


        /**
         * @param pAdaptationData Mandatory java.lang.String parameter.
         * @param cch Mandatory int parameter.
         */

        @VTID(22)
        void setAdaptationData(
          @MarshalAs(NativeType.Unicode) java.lang.String pAdaptationData,
          int cch);


        /**
         * @param dwReserved Mandatory int parameter.
         */

        @VTID(23)
        void pause(
          int dwReserved);


        /**
         * @param dwReserved Mandatory int parameter.
         */

        @VTID(24)
        void resume(
          int dwReserved);


        /**
         * @param pVoice Mandatory se_tpb_speechgen2.external.win.sapi5.ISpVoice parameter.
         * @param fAllowFormatChanges Mandatory int parameter.
         */

        @VTID(25)
        void setVoice(
          se_tpb_speechgen2.external.win.sapi5.ISpVoice pVoice,
          int fAllowFormatChanges);


        /**
         * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpVoice
         */

        @VTID(26)
        se_tpb_speechgen2.external.win.sapi5.ISpVoice getVoice();


        /**
         * @param ullEventInterest Mandatory long parameter.
         */

        @VTID(27)
        void setVoicePurgeEvent(
          long ullEventInterest);


        /**
         * @return  Returns a value of type long
         */

        @VTID(28)
        long getVoicePurgeEvent();


        /**
         * @param eContextState Mandatory se_tpb_speechgen2.external.win.sapi5.SPCONTEXTSTATE parameter.
         */

        @VTID(29)
        void setContextState(
          se_tpb_speechgen2.external.win.sapi5.SPCONTEXTSTATE eContextState);


        /**
         * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SPCONTEXTSTATE
         */

        @VTID(30)
        se_tpb_speechgen2.external.win.sapi5.SPCONTEXTSTATE getContextState();


        // Properties:
      }
