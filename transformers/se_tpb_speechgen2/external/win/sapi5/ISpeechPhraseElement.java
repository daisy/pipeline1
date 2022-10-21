package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseElement Interface
 */
@IID("{E6176F96-E373-4801-B223-3B62C068C0B4}")
public interface ISpeechPhraseElement extends Com4jObject {
  // Methods:
  /**
   * <p>
   * AudioTimeOffset
   * </p>
   * <p>
   * Getter method for the COM property "AudioTimeOffset"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int audioTimeOffset();


  /**
   * <p>
   * AudioSizeTime
   * </p>
   * <p>
   * Getter method for the COM property "AudioSizeTime"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  int audioSizeTime();


  /**
   * <p>
   * AudioStreamOffset
   * </p>
   * <p>
   * Getter method for the COM property "AudioStreamOffset"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  int audioStreamOffset();


  /**
   * <p>
   * AudioSizeBytes
   * </p>
   * <p>
   * Getter method for the COM property "AudioSizeBytes"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  int audioSizeBytes();


  /**
   * <p>
   * RetainedStreamOffset
   * </p>
   * <p>
   * Getter method for the COM property "RetainedStreamOffset"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  int retainedStreamOffset();


  /**
   * <p>
   * RetainedSizeBytes
   * </p>
   * <p>
   * Getter method for the COM property "RetainedSizeBytes"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  int retainedSizeBytes();


  /**
   * <p>
   * DisplayText
   * </p>
   * <p>
   * Getter method for the COM property "DisplayText"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  java.lang.String displayText();


  /**
   * <p>
   * LexicalForm
   * </p>
   * <p>
   * Getter method for the COM property "LexicalForm"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  java.lang.String lexicalForm();


  /**
   * <p>
   * Pronunciation
   * </p>
   * <p>
   * Getter method for the COM property "Pronunciation"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object pronunciation();


  /**
   * <p>
   * DisplayAttributes
   * </p>
   * <p>
   * Getter method for the COM property "DisplayAttributes"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(16)
  se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes displayAttributes();


  /**
   * <p>
   * RequiredConfidence
   * </p>
   * <p>
   * Getter method for the COM property "RequiredConfidence"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(17)
  se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence requiredConfidence();


  /**
   * <p>
   * ActualConfidence
   * </p>
   * <p>
   * Getter method for the COM property "ActualConfidence"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(18)
  se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence actualConfidence();


  /**
   * <p>
   * EngineConfidence
   * </p>
   * <p>
   * Getter method for the COM property "EngineConfidence"
   * </p>
   * @return  Returns a value of type float
   */

  @DISPID(13) //= 0xd. The runtime will prefer the VTID if present
  @VTID(19)
  float engineConfidence();


  // Properties:
}
