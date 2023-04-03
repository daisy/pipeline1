package se_tpb_speechgen2.external.win.sapi5.events;

import com4j.*;

@IID("{A372ACD1-3BEF-4BBD-8FFB-CB3E2B416AF8}")
public abstract class _ISpeechVoiceEvents {
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
   */

  @DISPID(2)
  public void endStream(
    int streamNumber,
    java.lang.Object streamPosition) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * VoiceChange
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param voiceObjectToken Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken parameter.
   */

  @DISPID(3)
  public void voiceChange(
    int streamNumber,
    java.lang.Object streamPosition,
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken voiceObjectToken) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * Bookmark
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param bookmark Mandatory java.lang.String parameter.
   * @param bookmarkId Mandatory int parameter.
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
   * <p>
   * Word
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param characterPosition Mandatory int parameter.
   * @param length Mandatory int parameter.
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
   * <p>
   * Sentence
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param characterPosition Mandatory int parameter.
   * @param length Mandatory int parameter.
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
   * <p>
   * Phoneme
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param duration Mandatory int parameter.
   * @param nextPhoneId Mandatory short parameter.
   * @param feature Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechVisemeFeature parameter.
   * @param currentPhoneId Mandatory short parameter.
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
   * <p>
   * Viseme
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param duration Mandatory int parameter.
   * @param nextVisemeId Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechVisemeType parameter.
   * @param feature Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechVisemeFeature parameter.
   * @param currentVisemeId Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechVisemeType parameter.
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
   * <p>
   * AudioLevel
   * </p>
   * @param streamNumber Mandatory int parameter.
   * @param streamPosition Mandatory java.lang.Object parameter.
   * @param audioLevel Mandatory int parameter.
   */

  @DISPID(9)
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
   * @param streamPosition Mandatory int parameter.
   * @param engineData Mandatory java.lang.Object parameter.
   */

  @DISPID(10)
  public void enginePrivate(
    int streamNumber,
    int streamPosition,
    java.lang.Object engineData) {
        throw new UnsupportedOperationException();
  }


  // Properties:
}
