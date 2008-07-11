package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechVoice Interface
 */
@IID("{269316D8-57BD-11D2-9EEE-00C04F797396}")
public interface ISpeechVoice extends Com4jObject {
    /**
     * Status
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.ISpeechVoiceStatus status();

    /**
     * Voice
     */
    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken voice();

    /**
     * Voice
     */
    @VTID(9)
    void voice(
        se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken voice);

    /**
     * Gets the audio output object
     */
    @VTID(10)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken audioOutput();

    /**
     * Gets the audio output object
     */
    @VTID(11)
    void audioOutput(
        se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken audioOutput);

    /**
     * Gets the audio output stream
     */
    @VTID(12)
    se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream audioOutputStream();

    /**
     * Gets the audio output stream
     */
    @VTID(13)
    void audioOutputStream(
        se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream audioOutputStream);

    /**
     * Rate
     */
    @VTID(14)
    int rate();

    /**
     * Rate
     */
    @VTID(15)
    void rate(
        int rate);

    /**
     * Volume
     */
    @VTID(16)
    int volume();

    /**
     * Volume
     */
    @VTID(17)
    void volume(
        int volume);

    /**
     * AllowAudioOutputFormatChangesOnNextSet
     */
    @VTID(18)
    void allowAudioOutputFormatChangesOnNextSet(
        boolean allow);

    /**
     * AllowAudioOutputFormatChangesOnNextSet
     */
    @VTID(19)
    boolean allowAudioOutputFormatChangesOnNextSet();

    /**
     * EventInterests
     */
    @VTID(20)
    se_tpb_speechgen2.external.win.sapi5.SpeechVoiceEvents eventInterests();

    /**
     * EventInterests
     */
    @VTID(21)
    void eventInterests(
        se_tpb_speechgen2.external.win.sapi5.SpeechVoiceEvents eventInterestFlags);

    /**
     * Priority
     */
    @VTID(22)
    void priority(
        se_tpb_speechgen2.external.win.sapi5.SpeechVoicePriority priority);

    /**
     * Priority
     */
    @VTID(23)
    se_tpb_speechgen2.external.win.sapi5.SpeechVoicePriority priority();

    /**
     * AlertBoundary
     */
    @VTID(24)
    void alertBoundary(
        se_tpb_speechgen2.external.win.sapi5.SpeechVoiceEvents boundary);

    /**
     * AlertBoundary
     */
    @VTID(25)
    se_tpb_speechgen2.external.win.sapi5.SpeechVoiceEvents alertBoundary();

    /**
     * SyncSpeakTimeout
     */
    @VTID(26)
    void synchronousSpeakTimeout(
        int msTimeout);

    /**
     * SyncSpeakTimeout
     */
    @VTID(27)
    int synchronousSpeakTimeout();

    /**
     * Speak
     */
    @VTID(28)
    int speak(
        java.lang.String text,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechVoiceSpeakFlags flags);

    /**
     * SpeakStream
     */
    @VTID(29)
    int speakStream(
        se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream stream,
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.SpeechVoiceSpeakFlags flags);

    /**
     * Pauses the voices rendering.
     */
    @VTID(30)
    void pause();

    /**
     * Resumes the voices rendering.
     */
    @VTID(31)
    void resume();

    /**
     * Skips rendering the specified number of items.
     */
    @VTID(32)
    int skip(
        java.lang.String type,
        int numItems);

    /**
     * GetVoices
     */
    @VTID(33)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokens getVoices(
        @DefaultValue("")java.lang.String requiredAttributes,
        @DefaultValue("")java.lang.String optionalAttributes);

    /**
     * GetAudioOutputs
     */
    @VTID(34)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokens getAudioOutputs(
        @DefaultValue("")java.lang.String requiredAttributes,
        @DefaultValue("")java.lang.String optionalAttributes);

    /**
     * WaitUntilDone
     */
    @VTID(35)
    boolean waitUntilDone(
        int msTimeout);

    /**
     * SpeakCompleteEvent
     */
    @VTID(36)
    int speakCompleteEvent();

    /**
     * IsUISupported
     */
    @VTID(37)
    boolean isUISupported(
        java.lang.String typeOfUI,
        @DefaultValue("")java.lang.Object extraData);

    /**
     * DisplayUI
     */
    @VTID(38)
    void displayUI(
        int hWndParent,
        java.lang.String title,
        java.lang.String typeOfUI,
        @DefaultValue("")java.lang.Object extraData);

}
