package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpGrammarBuilder Interface
 */
@IID("{8137828F-591A-4A42-BE58-49EA7EBAAC68}")
public interface ISpGrammarBuilder extends Com4jObject {
  // Methods:
  /**
   * @param newLanguage Mandatory short parameter.
   */

  @VTID(3)
  void resetGrammar(
    short newLanguage);


  /**
   * @param pszRuleName Mandatory java.lang.String parameter.
   * @param dwRuleId Mandatory int parameter.
   * @param dwAttributes Mandatory int parameter.
   * @param fCreateIfNotExist Mandatory int parameter.
   * @return  Returns a value of type java.nio.Buffer
   */

  @VTID(4)
  java.nio.Buffer getRule(
    @MarshalAs(NativeType.Unicode) java.lang.String pszRuleName,
    int dwRuleId,
    int dwAttributes,
    int fCreateIfNotExist);


  /**
   * @param hState Mandatory java.nio.Buffer parameter.
   */

  @VTID(5)
  void clearRule(
    java.nio.Buffer hState);


  /**
   * @param hState Mandatory java.nio.Buffer parameter.
   * @return  Returns a value of type java.nio.Buffer
   */

  @VTID(6)
  java.nio.Buffer createNewState(
    java.nio.Buffer hState);


      /**
       * @param hRuleState Mandatory java.nio.Buffer parameter.
       * @param pszResourceName Mandatory java.lang.String parameter.
       * @param pszResourceValue Mandatory java.lang.String parameter.
       */

      @VTID(9)
      void addResource(
        java.nio.Buffer hRuleState,
        @MarshalAs(NativeType.Unicode) java.lang.String pszResourceName,
        @MarshalAs(NativeType.Unicode) java.lang.String pszResourceValue);


      /**
       * @param dwReserved Mandatory int parameter.
       */

      @VTID(10)
      void commit(
        int dwReserved);


      // Properties:
    }
