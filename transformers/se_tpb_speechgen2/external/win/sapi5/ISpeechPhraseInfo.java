package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseInfo Interface
 */
@IID("{961559CF-4E67-4662-8BF0-D93F1FCD61B3}")
public interface ISpeechPhraseInfo extends Com4jObject {
  // Methods:
  /**
   * <p>
   * LanguageId
   * </p>
   * <p>
   * Getter method for the COM property "LanguageId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int languageId();


  /**
   * <p>
   * GrammarId
   * </p>
   * <p>
   * Getter method for the COM property "GrammarId"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object grammarId();


  /**
   * <p>
   * StartTime
   * </p>
   * <p>
   * Getter method for the COM property "StartTime"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object startTime();


  /**
   * <p>
   * AudioStreamPosition
   * </p>
   * <p>
   * Getter method for the COM property "AudioStreamPosition"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object audioStreamPosition();


  /**
   * <p>
   * AudioSizeBytes
   * </p>
   * <p>
   * Getter method for the COM property "AudioSizeBytes"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  int audioSizeBytes();


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
   * AudioSizeTime
   * </p>
   * <p>
   * Getter method for the COM property "AudioSizeTime"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  int audioSizeTime();


  /**
   * <p>
   * Rule
   * </p>
   * <p>
   * Getter method for the COM property "Rule"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRule
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRule rule();


  /**
   * <p>
   * Properties
   * </p>
   * <p>
   * Getter method for the COM property "Properties"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperties
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperties properties();


  @VTID(15)
  @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperties.class})
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperty properties(
    int index);

  /**
   * <p>
   * Elements
   * </p>
   * <p>
   * Getter method for the COM property "Elements"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseElements
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(16)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseElements elements();


  @VTID(16)
  @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseElements.class})
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseElement elements(
    int index);

  /**
   * <p>
   * Replacements
   * </p>
   * <p>
   * Getter method for the COM property "Replacements"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseReplacements
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(17)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseReplacements replacements();


  @VTID(17)
  @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseReplacements.class})
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseReplacement replacements(
    int index);

  /**
   * <p>
   * EngineId
   * </p>
   * <p>
   * Getter method for the COM property "EngineId"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(18)
  java.lang.String engineId();


  /**
   * <p>
   * EnginePrivateData
   * </p>
   * <p>
   * Getter method for the COM property "EnginePrivateData"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(13) //= 0xd. The runtime will prefer the VTID if present
  @VTID(19)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object enginePrivateData();


  /**
   * <p>
   * SaveToMemory
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(20)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object saveToMemory();


  /**
   * <p>
   * GetText
   * </p>
   * @param startElement Optional parameter. Default value is 0
   * @param elements Optional parameter. Default value is -1
   * @param useReplacements Optional parameter. Default value is false
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(15) //= 0xf. The runtime will prefer the VTID if present
  @VTID(21)
  java.lang.String getText(
    @Optional @DefaultValue("0") int startElement,
    @Optional @DefaultValue("-1") int elements,
    @Optional @DefaultValue("-1") boolean useReplacements);


  /**
   * <p>
   * DisplayAttributes
   * </p>
   * @param startElement Optional parameter. Default value is 0
   * @param elements Optional parameter. Default value is -1
   * @param useReplacements Optional parameter. Default value is false
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(22)
  se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes getDisplayAttributes(
    @Optional @DefaultValue("0") int startElement,
    @Optional @DefaultValue("-1") int elements,
    @Optional @DefaultValue("-1") boolean useReplacements);


  // Properties:
}
