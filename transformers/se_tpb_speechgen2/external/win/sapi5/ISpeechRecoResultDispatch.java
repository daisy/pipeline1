package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecoResultDispatch Interface
 */
@IID("{6D60EB64-ACED-40A6-BBF3-4E557F71DEE2}")
public interface ISpeechRecoResultDispatch extends Com4jObject {
  // Methods:
  /**
   * <p>
   * RecoContext
   * </p>
   * <p>
   * Getter method for the COM property "RecoContext"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechRecoContext
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.ISpeechRecoContext recoContext();


  /**
   * <p>
   * Times
   * </p>
   * <p>
   * Getter method for the COM property "Times"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResultTimes
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.ISpeechRecoResultTimes times();


  /**
   * <p>
   * AudioFormat
   * </p>
   * <p>
   * Setter method for the COM property "AudioFormat"
   * </p>
   * @param format Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  void audioFormat(
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat format);


  /**
   * <p>
   * AudioFormat
   * </p>
   * <p>
   * Getter method for the COM property "AudioFormat"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(10)
  se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat audioFormat();


  /**
   * <p>
   * PhraseInfo
   * </p>
   * <p>
   * Getter method for the COM property "PhraseInfo"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseInfo
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(11)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseInfo phraseInfo();


  /**
   * <p>
   * Alternates
   * </p>
   * @param requestCount Mandatory int parameter.
   * @param startElement Optional parameter. Default value is 0
   * @param elements Optional parameter. Default value is -1
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseAlternates
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(12)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseAlternates alternates(
    int requestCount,
    @Optional @DefaultValue("0") int startElement,
    @Optional @DefaultValue("-1") int elements);


  /**
   * <p>
   * Audio
   * </p>
   * @param startElement Optional parameter. Default value is 0
   * @param elements Optional parameter. Default value is -1
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechMemoryStream
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(13)
  se_tpb_speechgen2.external.win.sapi5.ISpeechMemoryStream audio(
    @Optional @DefaultValue("0") int startElement,
    @Optional @DefaultValue("-1") int elements);


  /**
   * <p>
   * SpeakAudio
   * </p>
   * @param startElement Optional parameter. Default value is 0
   * @param elements Optional parameter. Default value is -1
   * @param flags Optional parameter. Default value is 0
   * @return  Returns a value of type int
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(14)
  int speakAudio(
    @Optional @DefaultValue("0") int startElement,
    @Optional @DefaultValue("-1") int elements,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechVoiceSpeakFlags flags);


  /**
   * <p>
   * SaveToMemory
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(15)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object saveToMemory();


  /**
   * <p>
   * DiscardResultInfo
   * </p>
   * @param valueTypes Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechDiscardType parameter.
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(16)
  void discardResultInfo(
    se_tpb_speechgen2.external.win.sapi5.SpeechDiscardType valueTypes);


  /**
   * <p>
   * GetXMLResult
   * </p>
   * @param options Mandatory se_tpb_speechgen2.external.win.sapi5.SPXMLRESULTOPTIONS parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(17)
  java.lang.String getXMLResult(
    se_tpb_speechgen2.external.win.sapi5.SPXMLRESULTOPTIONS options);


  /**
   * <p>
   * GetXMLErrorInfo
   * </p>
   * @param lineNumber Mandatory Holder<Integer> parameter.
   * @param scriptLine Mandatory Holder<java.lang.String> parameter.
   * @param source Mandatory Holder<java.lang.String> parameter.
   * @param description Mandatory Holder<java.lang.String> parameter.
   * @param resultCode Mandatory Holder<Integer> parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(18)
  boolean getXMLErrorInfo(
    Holder<Integer> lineNumber,
    Holder<java.lang.String> scriptLine,
    Holder<java.lang.String> source,
    Holder<java.lang.String> description,
    Holder<Integer> resultCode);


  /**
   * <p>
   * SetTextFeedback
   * </p>
   * @param feedback Mandatory java.lang.String parameter.
   * @param wasSuccessful Mandatory boolean parameter.
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(19)
  void setTextFeedback(
    java.lang.String feedback,
    boolean wasSuccessful);


  // Properties:
}
