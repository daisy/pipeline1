package org.daisy.util.wincom.sapi5.events;

import com4j.*;

@IID("{7B8FCB42-0E9D-4F00-A048-7B04D6179D3D}")
public abstract class _ISpeechRecoContextEvents {
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
        java.lang.Object streamPosition,
        boolean streamReleased) {
            throw new UnsupportedOperationException();
    }

    /**
     * Bookmark
     */
    @DISPID(3)
    public void bookmark(
        int streamNumber,
        java.lang.Object streamPosition,
        java.lang.Object bookmarkId,
        org.daisy.util.wincom.sapi5.SpeechBookmarkOptions options) {
            throw new UnsupportedOperationException();
    }

    /**
     * SoundStart
     */
    @DISPID(4)
    public void soundStart(
        int streamNumber,
        java.lang.Object streamPosition) {
            throw new UnsupportedOperationException();
    }

    /**
     * SoundEnd
     */
    @DISPID(5)
    public void soundEnd(
        int streamNumber,
        java.lang.Object streamPosition) {
            throw new UnsupportedOperationException();
    }

    /**
     * PhraseStart
     */
    @DISPID(6)
    public void phraseStart(
        int streamNumber,
        java.lang.Object streamPosition) {
            throw new UnsupportedOperationException();
    }

    /**
     * Recognition
     */
    @DISPID(7)
    public void recognition(
        int streamNumber,
        java.lang.Object streamPosition,
        org.daisy.util.wincom.sapi5.SpeechRecognitionType recognitionType,
        org.daisy.util.wincom.sapi5.ISpeechRecoResult result) {
            throw new UnsupportedOperationException();
    }

    /**
     * Hypothesis
     */
    @DISPID(8)
    public void hypothesis(
        int streamNumber,
        java.lang.Object streamPosition,
        org.daisy.util.wincom.sapi5.ISpeechRecoResult result) {
            throw new UnsupportedOperationException();
    }

    /**
     * PropertyNumberChange
     */
    @DISPID(9)
    public void propertyNumberChange(
        int streamNumber,
        java.lang.Object streamPosition,
        java.lang.String propertyName,
        int newNumberValue) {
            throw new UnsupportedOperationException();
    }

    /**
     * PropertyStringChange
     */
    @DISPID(10)
    public void propertyStringChange(
        int streamNumber,
        java.lang.Object streamPosition,
        java.lang.String propertyName,
        java.lang.String newStringValue) {
            throw new UnsupportedOperationException();
    }

    /**
     * FalseRecognition
     */
    @DISPID(11)
    public void falseRecognition(
        int streamNumber,
        java.lang.Object streamPosition,
        org.daisy.util.wincom.sapi5.ISpeechRecoResult result) {
            throw new UnsupportedOperationException();
    }

    /**
     * Interference
     */
    @DISPID(12)
    public void interference(
        int streamNumber,
        java.lang.Object streamPosition,
        org.daisy.util.wincom.sapi5.SpeechInterference interference) {
            throw new UnsupportedOperationException();
    }

    /**
     * RequestUI
     */
    @DISPID(13)
    public void requestUI(
        int streamNumber,
        java.lang.Object streamPosition,
        java.lang.String uiType) {
            throw new UnsupportedOperationException();
    }

    /**
     * RecognizerStateChange
     */
    @DISPID(14)
    public void recognizerStateChange(
        int streamNumber,
        java.lang.Object streamPosition,
        org.daisy.util.wincom.sapi5.SpeechRecognizerState newState) {
            throw new UnsupportedOperationException();
    }

    /**
     * Adaptation
     */
    @DISPID(15)
    public void adaptation(
        int streamNumber,
        java.lang.Object streamPosition) {
            throw new UnsupportedOperationException();
    }

    /**
     * RecognitionForOtherContext
     */
    @DISPID(16)
    public void recognitionForOtherContext(
        int streamNumber,
        java.lang.Object streamPosition) {
            throw new UnsupportedOperationException();
    }

    /**
     * AudioLevel
     */
    @DISPID(17)
    public void audioLevel(
        int streamNumber,
        java.lang.Object streamPosition,
        int audioLevel) {
            throw new UnsupportedOperationException();
    }

    /**
     * EnginePrivate
     */
    @DISPID(18)
    public void enginePrivate(
        int streamNumber,
        java.lang.Object streamPosition,
        java.lang.Object engineData) {
            throw new UnsupportedOperationException();
    }

}
