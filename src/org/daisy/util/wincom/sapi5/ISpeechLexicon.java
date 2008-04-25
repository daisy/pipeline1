package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechLexicon Interface
 */
@IID("{3DA7627A-C7AE-4B23-8708-638C50362C25}")
public interface ISpeechLexicon extends Com4jObject {
    /**
     * GenerationId
     */
    @VTID(7)
    int generationId();

    /**
     * GetWords
     */
    @VTID(8)
    org.daisy.util.wincom.sapi5.ISpeechLexiconWords getWords(
        @DefaultValue("3")org.daisy.util.wincom.sapi5.SpeechLexiconType flags,
        @DefaultValue("0")Holder<Integer> generationId);

    /**
     * AddPronunciation
     */
    @VTID(9)
    void addPronunciation(
        java.lang.String bstrWord,
        int langId,
        @DefaultValue("0")org.daisy.util.wincom.sapi5.SpeechPartOfSpeech partOfSpeech,
        @DefaultValue("")java.lang.String bstrPronunciation);

    /**
     * AddPronunciationByPhoneIds
     */
    @VTID(10)
    void addPronunciationByPhoneIds(
        java.lang.String bstrWord,
        int langId,
        @DefaultValue("0")org.daisy.util.wincom.sapi5.SpeechPartOfSpeech partOfSpeech,
        @DefaultValue("")java.lang.Object phoneIds);

    /**
     * RemovePronunciation
     */
    @VTID(11)
    void removePronunciation(
        java.lang.String bstrWord,
        int langId,
        @DefaultValue("0")org.daisy.util.wincom.sapi5.SpeechPartOfSpeech partOfSpeech,
        @DefaultValue("")java.lang.String bstrPronunciation);

    /**
     * RemovePronunciationByPhoneIds
     */
    @VTID(12)
    void removePronunciationByPhoneIds(
        java.lang.String bstrWord,
        int langId,
        @DefaultValue("0")org.daisy.util.wincom.sapi5.SpeechPartOfSpeech partOfSpeech,
        @DefaultValue("")java.lang.Object phoneIds);

    /**
     * GetPronunciations
     */
    @VTID(13)
    org.daisy.util.wincom.sapi5.ISpeechLexiconPronunciations getPronunciations(
        java.lang.String bstrWord,
        @DefaultValue("0")int langId,
        @DefaultValue("3")org.daisy.util.wincom.sapi5.SpeechLexiconType typeFlags);

    /**
     * GetGenerationChange
     */
    @VTID(14)
    org.daisy.util.wincom.sapi5.ISpeechLexiconWords getGenerationChange(
        Holder<Integer> generationId);

}
