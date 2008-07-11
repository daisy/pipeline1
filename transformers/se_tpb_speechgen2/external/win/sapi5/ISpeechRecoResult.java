package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecoResult Interface
 */
@IID("{ED2879CF-CED9-4EE6-A534-DE0191D5468D}")
public interface ISpeechRecoResult extends Com4jObject {
    /**
     * RecoContext
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoContext recoContext();

    /**
     * Times
     */
    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResultTimes times();

    /**
     * AudioFormat
     */
    @VTID(9)
    void audioFormat(
        se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat format);

    /**
     * AudioFormat
     */
    @VTID(10)
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat audioFormat();

    /**
     * PhraseInfo
     */
    @VTID(11)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseInfo phraseInfo();

    /**
     * Alternates
     */
    @VTID(12)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseAlternates alternates(
        int requestCount,
        @DefaultValue("0")int startElement,
        @DefaultValue("-1")int elements);

    /**
     * Audio
     */
    @VTID(13)
    se_tpb_speechgen2.external.win.sapi5.ISpeechMemoryStream audio(
        @DefaultValue("0")int startElement,
        @DefaultValue("-1")int elements);

    /**
     * SpeakAudio
     */
    @VTID(14)
    int speakAudio(
        @DefaultValue("0")int startElement,
        @DefaultValue("-1")int elements,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechVoiceSpeakFlags flags);

    /**
     * SaveToMemory
     */
    @VTID(15)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object saveToMemory();

    /**
     * DiscardResultInfo
     */
    @VTID(16)
    void discardResultInfo(
        se_tpb_speechgen2.external.win.sapi5.SpeechDiscardType valueTypes);

}
