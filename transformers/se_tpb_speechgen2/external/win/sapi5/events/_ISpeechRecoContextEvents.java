package se_tpb_speechgen2.external.win.sapi5.events;

import com4j.*;

@IID("{7B8FCB42-0E9D-4F00-A048-7B04D6179D3D}")
public abstract class _ISpeechRecoContextEvents {
  // Methods:
  /**
   * <p>
   * StartStream
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   */

  @DISPID(1)
  public void startStream(
    int streamNumber,
    java.lang.Object streamPosition) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * EndStream
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param streamReleased Mandatory boolean parameter.
   */

  @DISPID(2)
  public void endStream(
    int streamNumber,
    java.lang.Object streamPosition,
    boolean streamReleased) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * Bookmark
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param bookmarkId Mandatory java.lang.Object parameter.
   * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechBookmarkOptions parameter.
   */

  @DISPID(3)
  public void bookmark(
    int streamNumber,
    java.lang.Object streamPosition,
    java.lang.Object bookmarkId,
    se_tpb_speechgen2.external.win.sapi5.SpeechBookmarkOptions options) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * SoundStart
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   */

  @DISPID(4)
  public void soundStart(
    int streamNumber,
    java.lang.Object streamPosition) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * SoundEnd
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   */

  @DISPID(5)
  public void soundEnd(
    int streamNumber,
    java.lang.Object streamPosition) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * PhraseStart
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   */

  @DISPID(6)
  public void phraseStart(
    int streamNumber,
    java.lang.Object streamPosition) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * Recognition
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param recognitionType Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechRecognitionType parameter.
   * @param result Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult parameter.
   */

  @DISPID(7)
  public void recognition(
    int streamNumber,
    java.lang.Object streamPosition,
    se_tpb_speechgen2.external.win.sapi5.SpeechRecognitionType recognitionType,
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult result) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * Hypothesis
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param result Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult parameter.
   */

  @DISPID(8)
  public void hypothesis(
    int streamNumber,
    java.lang.Object streamPosition,
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult result) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * PropertyNumberChange
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param propertyName Mandatory java.lang.String parameter.
   * @param newNumberValue Mandatory int parameter.
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
   * <p>
   * PropertyStringChange
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param propertyName Mandatory java.lang.String parameter.
   * @param newStringValue Mandatory java.lang.String parameter.
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
   * <p>
   * FalseRecognition
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param result Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult parameter.
   */

  @DISPID(11)
  public void falseRecognition(
    int streamNumber,
    java.lang.Object streamPosition,
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResult result) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * Interference
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param interference Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechInterference parameter.
   */

  @DISPID(12)
  public void interference(
    int streamNumber,
    java.lang.Object streamPosition,
    se_tpb_speechgen2.external.win.sapi5.SpeechInterference interference) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * RequestUI
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param uiType Mandatory java.lang.String parameter.
   */

  @DISPID(13)
  public void requestUI(
    int streamNumber,
    java.lang.Object streamPosition,
    java.lang.String uiType) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * RecognizerStateChange
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param newState Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechRecognizerState parameter.
   */

  @DISPID(14)
  public void recognizerStateChange(
    int streamNumber,
    java.lang.Object streamPosition,
    se_tpb_speechgen2.external.win.sapi5.SpeechRecognizerState newState) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * Adaptation
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   */

  @DISPID(15)
  public void adaptation(
    int streamNumber,
    java.lang.Object streamPosition) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * RecognitionForOtherContext
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   */

  @DISPID(16)
  public void recognitionForOtherContext(
    int streamNumber,
    java.lang.Object streamPosition) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * AudioLevel
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param audioLevel Mandatory int parameter.
   */

  @DISPID(17)
  public void audioLevel(
    int streamNumber,
    java.lang.Object streamPosition,
    int audioLevel) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * EnginePrivate
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param engineData Mandatory java.lang.Object parameter.
   */

  @DISPID(18)
  public void enginePrivate(
    int streamNumber,
    java.lang.Object streamPosition,
    java.lang.Object engineData) {
        throw new UnsupportedOperationException();
  }


  // Properties:
}
