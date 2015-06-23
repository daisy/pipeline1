package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseInfo Interface
 */
@IID("{961559CF-4E67-4662-8BF0-D93F1FCD61B3}")
public interface ISpeechPhraseInfo extends Com4jObject {
    /**
     * LanguageId
     */
    @VTID(7)
    int languageId();

    /**
     * GrammarId
     */
    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object grammarId();

    /**
     * StartTime
     */
    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object startTime();

    /**
     * AudioStreamPosition
     */
    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object audioStreamPosition();

    /**
     * AudioSizeBytes
     */
    @VTID(11)
    int audioSizeBytes();

    /**
     * RetainedSizeBytes
     */
    @VTID(12)
    int retainedSizeBytes();

    /**
     * AudioSizeTime
     */
    @VTID(13)
    int audioSizeTime();

    /**
     * Rule
     */
    @VTID(14)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRule rule();

    /**
     * Properties
     */
    @VTID(15)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperties properties();

    @VTID(15)
    @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperties.class})
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperty properties(
        int index);

    /**
     * Elements
     */
    @VTID(16)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseElements elements();

    @VTID(16)
    @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseElements.class})
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseElement elements(
        int index);

    /**
     * Replacements
     */
    @VTID(17)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseReplacements replacements();

    @VTID(17)
    @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseReplacements.class})
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseReplacement replacements(
        int index);

    /**
     * EngineId
     */
    @VTID(18)
    java.lang.String engineId();

    /**
     * EnginePrivateData
     */
    @VTID(19)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object enginePrivateData();

    /**
     * SaveToMemory
     */
    @VTID(20)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object saveToMemory();

    /**
     * GetText
     */
    @VTID(21)
    java.lang.String getText(
        @DefaultValue("0")int startElement,
        @DefaultValue("-1")int elements,
        @DefaultValue("-1")boolean useReplacements);

    /**
     * DisplayAttributes
     */
    @VTID(22)
    se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes getDisplayAttributes(
        @DefaultValue("0")int startElement,
        @DefaultValue("-1")int elements,
        @DefaultValue("-1")boolean useReplacements);

}
