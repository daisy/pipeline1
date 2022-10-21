package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecoGrammar2 Interface
 */
@IID("{4B37BC9E-9ED6-44A3-93D3-18F022B79EC3}")
public interface ISpRecoGrammar2 extends Com4jObject {
  // Methods:
    /**
     * @param pszFileName Mandatory java.lang.String parameter.
     * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS parameter.
     * @param pszSharingUri Mandatory java.lang.String parameter.
     * @param pszBaseUri Mandatory java.lang.String parameter.
     */

    @VTID(4)
    void loadCmdFromFile2(
      @MarshalAs(NativeType.Unicode) java.lang.String pszFileName,
      se_tpb_speechgen2.external.win.sapi5.SPLOADOPTIONS options,
      @MarshalAs(NativeType.Unicode) java.lang.String pszSharingUri,
      @MarshalAs(NativeType.Unicode) java.lang.String pszBaseUri);


      /**
       * @param pszRuleName Mandatory java.lang.String parameter.
       * @param ulRuleId Mandatory int parameter.
       * @param nRulePriority Mandatory int parameter.
       */

      @VTID(6)
      void setRulePriority(
        @MarshalAs(NativeType.Unicode) java.lang.String pszRuleName,
        int ulRuleId,
        int nRulePriority);


      /**
       * @param pszRuleName Mandatory java.lang.String parameter.
       * @param ulRuleId Mandatory int parameter.
       * @param flWeight Mandatory float parameter.
       */

      @VTID(7)
      void setRuleWeight(
        @MarshalAs(NativeType.Unicode) java.lang.String pszRuleName,
        int ulRuleId,
        float flWeight);


      /**
       * @param flWeight Mandatory float parameter.
       */

      @VTID(8)
      void setDictationWeight(
        float flWeight);


      /**
       * @param pLoader Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechResourceLoader parameter.
       */

      @VTID(9)
      void setGrammarLoader(
        se_tpb_speechgen2.external.win.sapi5.ISpeechResourceLoader pLoader);


      /**
       * @param pSMLSecurityManager Mandatory se_tpb_speechgen2.external.win.sapi5.IInternetSecurityManager parameter.
       */

      @VTID(10)
      void setSMLSecurityManager(
        se_tpb_speechgen2.external.win.sapi5.IInternetSecurityManager pSMLSecurityManager);


      // Properties:
    }
