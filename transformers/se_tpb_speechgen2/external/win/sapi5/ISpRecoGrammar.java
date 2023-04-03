package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecoGrammar Interface
 */
@IID("{2177DB29-7F45-47D0-8554-067E91C80502}")
public interface ISpRecoGrammar extends se_tpb_speechgen2.external.win.sapi5.ISpGrammarBuilder {
  // Methods:
  /**
   * @return  Returns a value of type long
   */

  @VTID(11)
  long getGrammarId();


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpRecoContext
   */

  @VTID(12)
  se_tpb_speechgen2.external.win.sapi5.ISpRecoContext getRecoContext();


  /**
   * @param pszFileName Mandatory java.lang.String parameter.
   * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS parameter.
   */

  @VTID(13)
  void loadCmdFromFile(
    @MarshalAs(NativeType.Unicode) java.lang.String pszFileName,
    se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);


  /**
   * @param rcid Mandatory GUID parameter.
   * @param pszGrammarName Mandatory java.lang.String parameter.
   * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS parameter.
   */

  @VTID(14)
  void loadCmdFromObject(
    GUID rcid,
    @MarshalAs(NativeType.Unicode) java.lang.String pszGrammarName,
    se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);


  /**
   * @param hModule Mandatory java.nio.Buffer parameter.
   * @param pszResourceName Mandatory java.lang.String parameter.
   * @param pszResourceType Mandatory java.lang.String parameter.
   * @param wLanguage Mandatory short parameter.
   * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS parameter.
   */

  @VTID(15)
  void loadCmdFromResource(
    java.nio.Buffer hModule,
    @MarshalAs(NativeType.Unicode) java.lang.String pszResourceName,
    @MarshalAs(NativeType.Unicode) java.lang.String pszResourceType,
    short wLanguage,
    se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);


    /**
     * @param rguidParam Mandatory GUID parameter.
     * @param pszStringParam Mandatory java.lang.String parameter.
     * @param pvDataPrarm Mandatory java.nio.Buffer parameter.
     * @param cbDataSize Mandatory int parameter.
     * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS parameter.
     */

    @VTID(17)
    void loadCmdFromProprietaryGrammar(
      GUID rguidParam,
      @MarshalAs(NativeType.Unicode) java.lang.String pszStringParam,
      java.nio.Buffer pvDataPrarm,
      int cbDataSize,
      se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);


    /**
     * @param pszName Mandatory java.lang.String parameter.
     * @param pReserved Mandatory java.nio.Buffer parameter.
     * @param newState Mandatory se_tpb_speechgen2.external.win.sapi5.SPRULESTATE parameter.
     */

    @VTID(18)
    void setRuleState(
      @MarshalAs(NativeType.Unicode) java.lang.String pszName,
      java.nio.Buffer pReserved,
      se_tpb_speechgen2.external.win.sapi5.SPRULESTATE newState);


    /**
     * @param ulRuleId Mandatory int parameter.
     * @param newState Mandatory se_tpb_speechgen2.external.win.sapi5.SPRULESTATE parameter.
     */

    @VTID(19)
    void setRuleIdState(
      int ulRuleId,
      se_tpb_speechgen2.external.win.sapi5.SPRULESTATE newState);


    /**
     * @param pszTopicName Mandatory java.lang.String parameter.
     * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS parameter.
     */

    @VTID(20)
    void loadDictation(
      @MarshalAs(NativeType.Unicode) java.lang.String pszTopicName,
      se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options);


    /**
     */

    @VTID(21)
    void unloadDictation();


    /**
     * @param newState Mandatory se_tpb_speechgen2.external.win.sapi5.SPRULESTATE parameter.
     */

    @VTID(22)
    void setDictationState(
      se_tpb_speechgen2.external.win.sapi5.SPRULESTATE newState);


        /**
         * @param pszWord Mandatory java.lang.String parameter.
         * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SPWORDPRONOUNCEABLE
         */

        @VTID(25)
        se_tpb_speechgen2.external.win.sapi5.SPWORDPRONOUNCEABLE isPronounceable(
          @MarshalAs(NativeType.Unicode) java.lang.String pszWord);


        /**
         * @param eGrammarState Mandatory se_tpb_speechgen2.external.win.sapi5.SPGRAMMARSTATE parameter.
         */

        @VTID(26)
        void setGrammarState(
          se_tpb_speechgen2.external.win.sapi5.SPGRAMMARSTATE eGrammarState);


        /**
         * @param pStream Mandatory se_tpb_speechgen2.external.win.sapi5.IStream parameter.
         * @return  Returns a value of type java.lang.String
         */

        @VTID(27)
        @ReturnValue(type=NativeType.Unicode)
        java.lang.String saveCmd(
          se_tpb_speechgen2.external.win.sapi5.IStream pStream);


        /**
         * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SPGRAMMARSTATE
         */

        @VTID(28)
        se_tpb_speechgen2.external.win.sapi5.SPGRAMMARSTATE getGrammarState();


        // Properties:
      }
