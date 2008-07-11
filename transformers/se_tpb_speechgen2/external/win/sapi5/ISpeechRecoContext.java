package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecoContext Interface
 */
@IID("{580AA49D-7E1E-4809-B8E2-57DA806104B8}")
public interface ISpeechRecoContext extends Com4jObject {
    /**
     * Recognizer
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecognizer recognizer();

    /**
     * AudioInInterferenceStatus
     */
    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.SpeechInterference audioInputInterferenceStatus();

    /**
     * RequestedUIType
     */
    @VTID(9)
    java.lang.String requestedUIType();

    /**
     * Voice
     */
    @VTID(10)
    void voice(
        se_tpb_speechgen2.external.win.sapi5.ISpeechVoice voice);

    /**
     * Voice
     */
    @VTID(11)
    se_tpb_speechgen2.external.win.sapi5.ISpeechVoice voice();

    /**
     * AllowVoiceFormatMatchingOnNextSet
     */
    @VTID(12)
    void allowVoiceFormatMatchingOnNextSet(
        boolean pAllow);

    /**
     * AllowVoiceFormatMatchingOnNextSet
     */
    @VTID(13)
    boolean allowVoiceFormatMatchingOnNextSet();

    /**
     * VoicePurgeEvent
     */
    @VTID(14)
    void voicePurgeEvent(
        se_tpb_speechgen2.external.win.sapi5.SpeechRecoEvents eventInterest);

    /**
     * VoicePurgeEvent
     */
    @VTID(15)
    se_tpb_speechgen2.external.win.sapi5.SpeechRecoEvents voicePurgeEvent();

    /**
     * EventInterests
     */
    @VTID(16)
    void eventInterests(
        se_tpb_speechgen2.external.win.sapi5.SpeechRecoEvents eventInterest);

    /**
     * EventInterests
     */
    @VTID(17)
    se_tpb_speechgen2.external.win.sapi5.SpeechRecoEvents eventInterests();

    /**
     * CmdMaxAlternates
     */
    @VTID(18)
    void cmdMaxAlternates(
        int maxAlternates);

    /**
     * CmdMaxAlternates
     */
    @VTID(19)
    int cmdMaxAlternates();

    /**
     * State
     */
    @VTID(20)
    void state(
        se_tpb_speechgen2.external.win.sapi5.SpeechRecoContextState state);

    /**
     * State
     */
    @VTID(21)
    se_tpb_speechgen2.external.win.sapi5.SpeechRecoContextState state();

    /**
     * RetainedAudio
     */
    @VTID(22)
    void retainedAudio(
        se_tpb_speechgen2.external.win.sapi5.SpeechRetainedAudioOptions option);

    /**
     * RetainedAudio
     */
    @VTID(23)
    se_tpb_speechgen2.external.win.sapi5.SpeechRetainedAudioOptions retainedAudio();

    /**
     * RetainedAudioFormat
     */
    @VTID(24)
    void retainedAudioFormat(
        se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat format);

    /**
     * RetainedAudioFormat
     */
    @VTID(25)
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat retainedAudioFormat();

    /**
     * Pause
     */
    @VTID(26)
    void pause();

    /**
     * Resume
     */
    @VTID(27)
    void resume();

    /**
     * CreateGrammar
     */
    @VTID(28)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoGrammar createGrammar(
        @MarshalAs(NativeType.VARIANT) @DefaultValue("0")java.lang.Object grammarId);

    /**
     * CreateResultFromMemory
     */
    @VTID(29)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult createResultFromMemory(
        java.lang.Object resultBlock);

    /**
     * Bookmark
     */
    @VTID(30)
    void bookmark(
        se_tpb_speechgen2.external.win.sapi5.SpeechBookmarkOptions options,
        @MarshalAs(NativeType.VARIANT) java.lang.Object streamPos,
        @MarshalAs(NativeType.VARIANT) java.lang.Object bookmarkId);

    /**
     * SetAdaptationData
     */
    @VTID(31)
    void setAdaptationData(
        java.lang.String adaptationString);

}
