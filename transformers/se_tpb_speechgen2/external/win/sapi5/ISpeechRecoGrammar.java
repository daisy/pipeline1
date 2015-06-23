package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecoGrammar Interface
 */
@IID("{B6D6F79F-2158-4E50-B5BC-9A9CCD852A09}")
public interface ISpeechRecoGrammar extends Com4jObject {
    /**
     * Id
     */
    @VTID(7)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object id();

    /**
     * RecoContext
     */
    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoContext recoContext();

    /**
     * State
     */
    @VTID(9)
    void state(
        se_tpb_speechgen2.external.win.sapi5.SpeechGrammarState state);

    /**
     * State
     */
    @VTID(10)
    se_tpb_speechgen2.external.win.sapi5.SpeechGrammarState state();

    /**
     * Rules
     */
    @VTID(11)
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRules rules();

    @VTID(11)
    @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRules.class})
    se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule rules(
        int index);

    /**
     * Reset
     */
    @VTID(12)
    void reset(
        @DefaultValue("0")int newLanguage);

    /**
     * CmdLoadFromFile
     */
    @VTID(13)
    void cmdLoadFromFile(
        java.lang.String fileName,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);

    /**
     * CmdLoadFromObject
     */
    @VTID(14)
    void cmdLoadFromObject(
        java.lang.String classId,
        java.lang.String grammarName,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);

    /**
     * CmdLoadFromResource
     */
    @VTID(15)
    void cmdLoadFromResource(
        int hModule,
        @MarshalAs(NativeType.VARIANT) java.lang.Object resourceName,
        @MarshalAs(NativeType.VARIANT) java.lang.Object resourceType,
        int languageId,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);

    /**
     * CmdLoadFromMemory
     */
    @VTID(16)
    void cmdLoadFromMemory(
        @MarshalAs(NativeType.VARIANT) java.lang.Object grammarData,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);

    /**
     * CmdLoadFromProprietaryGrammar
     */
    @VTID(17)
    void cmdLoadFromProprietaryGrammar(
        java.lang.String proprietaryGuid,
        java.lang.String proprietaryString,
        @MarshalAs(NativeType.VARIANT) java.lang.Object proprietaryData,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);

    /**
     * CmdSetRuleState
     */
    @VTID(18)
    void cmdSetRuleState(
        java.lang.String name,
        se_tpb_speechgen2.external.win.sapi5.SpeechRuleState state);

    /**
     * CmdSetRuleIdState
     */
    @VTID(19)
    void cmdSetRuleIdState(
        int ruleId,
        se_tpb_speechgen2.external.win.sapi5.SpeechRuleState state);

    /**
     * DictationLoad
     */
    @VTID(20)
    void dictationLoad(
        @DefaultValue("")java.lang.String topicName,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);

    /**
     * DictationUnload
     */
    @VTID(21)
    void dictationUnload();

    /**
     * DictationSetState
     */
    @VTID(22)
    void dictationSetState(
        se_tpb_speechgen2.external.win.sapi5.SpeechRuleState state);

    /**
     * SetWordSequenceData
     */
    @VTID(23)
    void setWordSequenceData(
        java.lang.String text,
        int textLength,
        se_tpb_speechgen2.external.win.sapi5.ISpeechTextSelectionInformation info);

    /**
     * SetTextSelection
     */
    @VTID(24)
    void setTextSelection(
        se_tpb_speechgen2.external.win.sapi5.ISpeechTextSelectionInformation info);

    /**
     * IsPronounceable
     */
    @VTID(25)
    se_tpb_speechgen2.external.win.sapi5.SpeechWordPronounceable isPronounceable(
        java.lang.String word);

}
