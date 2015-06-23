package se_tpb_speechgen2.external.win.sapi5.events;

import com4j.*;

@SuppressWarnings("unused")
@IID("{A372ACD1-3BEF-4BBD-8FFB-CB3E2B416AF8}")
public abstract class _ISpeechVoiceEvents {
    /**
     * StartStream
     */
    @DISPID(1)
    public void startStream(
        int streamNumber,
        java.lang.Object streamPosition) {
            throw new UnsupportedOperationException();
    }

    /**
     * EndStream
     */
    @DISPID(2)
    public void endStream(
        int streamNumber,
        java.lang.Object streamPosition) {
            throw new UnsupportedOperationException();
    }

    /**
     * VoiceChange
     */
    @DISPID(3)
    public void voiceChange(
        int streamNumber,
        java.lang.Object streamPosition,
        se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken voiceObjectToken) {
            throw new UnsupportedOperationException();
    }

    /**
     * Bookmark
     */
    @DISPID(4)
    public void bookmark(
        int streamNumber,
        java.lang.Object streamPosition,
        java.lang.String bookmark,
        int bookmarkId) {
            throw new UnsupportedOperationException();
    }

    /**
     * Word
     */
    @DISPID(5)
    public void word(
        int streamNumber,
        java.lang.Object streamPosition,
        int characterPosition,
        int length) {
            throw new UnsupportedOperationException();
    }

    /**
     * Sentence
     */
    @DISPID(7)
    public void sentence(
        int streamNumber,
        java.lang.Object streamPosition,
        int characterPosition,
        int length) {
            throw new UnsupportedOperationException();
    }

    /**
     * Phoneme
     */
    @DISPID(6)
    public void phoneme(
        int streamNumber,
        java.lang.Object streamPosition,
        int duration,
        short nextPhoneId,
        se_tpb_speechgen2.external.win.sapi5.SpeechVisemeFeature feature,
        short currentPhoneId) {
            throw new UnsupportedOperationException();
    }

    /**
     * Viseme
     */
    @DISPID(8)
    public void viseme(
        int streamNumber,
        java.lang.Object streamPosition,
        int duration,
        se_tpb_speechgen2.external.win.sapi5.SpeechVisemeType nextVisemeId,
        se_tpb_speechgen2.external.win.sapi5.SpeechVisemeFeature feature,
        se_tpb_speechgen2.external.win.sapi5.SpeechVisemeType currentVisemeId) {
            throw new UnsupportedOperationException();
    }

    /**
     * AudioLevel
     */
    @DISPID(9)
    public void audioLevel(
        int streamNumber,
        java.lang.Object streamPosition,
        int audioLevel) {
            throw new UnsupportedOperationException();
    }

    /**
     * EnginePrivate
     */
    @DISPID(10)
    public void enginePrivate(
        int streamNumber,
        int streamPosition,
        java.lang.Object engineData) {
            throw new UnsupportedOperationException();
    }

}
