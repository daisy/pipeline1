package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpGrammarBuilder Interface
 */
@IID("{8137828F-591A-4A42-BE58-49EA7EBAAC68}")
public interface ISpGrammarBuilder extends Com4jObject {
    @VTID(3)
    void resetGrammar(
        short newLanguage);

    @VTID(4)
    java.nio.Buffer getRule(
        @MarshalAs(NativeType.Unicode) java.lang.String pszRuleName,
        int dwRuleId,
        int dwAttributes,
        int fCreateIfNotExist);

    @VTID(5)
    void clearRule(
        java.nio.Buffer hState);

    @VTID(6)
    void createNewState(
        java.nio.Buffer hState,
        Holder<java.nio.Buffer> phState);

            @VTID(9)
            void addResource(
                java.nio.Buffer hRuleState,
                @MarshalAs(NativeType.Unicode) java.lang.String pszResourceName,
                @MarshalAs(NativeType.Unicode) java.lang.String pszResourceValue);

            @VTID(10)
            void commit(
                int dwReserved);

        }
