package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecoGrammar Interface
 */
@IID("{B6D6F79F-2158-4E50-B5BC-9A9CCD852A09}")
public interface ISpeechRecoGrammar extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Id
   * </p>
   * <p>
   * Getter method for the COM property "Id"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object id();


  /**
   * <p>
   * RecoContext
   * </p>
   * <p>
   * Getter method for the COM property "RecoContext"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechRecoContext
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.ISpeechRecoContext recoContext();


  /**
   * <p>
   * State
   * </p>
   * <p>
   * Setter method for the COM property "State"
   * </p>
   * @param state Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechGrammarState parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  void state(
    se_tpb_speechgen2.external.win.sapi5.SpeechGrammarState state);


  /**
   * <p>
   * State
   * </p>
   * <p>
   * Getter method for the COM property "State"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechGrammarState
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(10)
  se_tpb_speechgen2.external.win.sapi5.SpeechGrammarState state();


  /**
   * <p>
   * Rules
   * </p>
   * <p>
   * Getter method for the COM property "Rules"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRules
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(11)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRules rules();


  @VTID(11)
  @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRules.class})
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule rules(
    int index);

  /**
   * <p>
   * Reset
   * </p>
   * @param newLanguage Optional parameter. Default value is 0
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(12)
  void reset(
    @Optional @DefaultValue("0") int newLanguage);


  /**
   * <p>
   * CmdLoadFromFile
   * </p>
   * @param fileName Mandatory java.lang.String parameter.
   * @param loadOption Optional parameter. Default value is 0
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  void cmdLoadFromFile(
    java.lang.String fileName,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);


  /**
   * <p>
   * CmdLoadFromObject
   * </p>
   * @param classId Mandatory java.lang.String parameter.
   * @param grammarName Mandatory java.lang.String parameter.
   * @param loadOption Optional parameter. Default value is 0
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  void cmdLoadFromObject(
    java.lang.String classId,
    java.lang.String grammarName,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);


  /**
   * <p>
   * CmdLoadFromResource
   * </p>
   * @param hModule Mandatory int parameter.
   * @param resourceName Mandatory java.lang.Object parameter.
   * @param resourceType Mandatory java.lang.Object parameter.
   * @param languageId Mandatory int parameter.
   * @param loadOption Optional parameter. Default value is 0
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  void cmdLoadFromResource(
    int hModule,
    @MarshalAs(NativeType.VARIANT) java.lang.Object resourceName,
    @MarshalAs(NativeType.VARIANT) java.lang.Object resourceType,
    int languageId,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);


  /**
   * <p>
   * CmdLoadFromMemory
   * </p>
   * @param grammarData Mandatory java.lang.Object parameter.
   * @param loadOption Optional parameter. Default value is 0
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(16)
  void cmdLoadFromMemory(
    @MarshalAs(NativeType.VARIANT) java.lang.Object grammarData,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);


  /**
   * <p>
   * CmdLoadFromProprietaryGrammar
   * </p>
   * @param proprietaryGuid Mandatory java.lang.String parameter.
   * @param proprietaryString Mandatory java.lang.String parameter.
   * @param proprietaryData Mandatory java.lang.Object parameter.
   * @param loadOption Optional parameter. Default value is 0
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(17)
  void cmdLoadFromProprietaryGrammar(
    java.lang.String proprietaryGuid,
    java.lang.String proprietaryString,
    @MarshalAs(NativeType.VARIANT) java.lang.Object proprietaryData,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);


  /**
   * <p>
   * CmdSetRuleState
   * </p>
   * @param name Mandatory java.lang.String parameter.
   * @param state Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechRuleState parameter.
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(18)
  void cmdSetRuleState(
    java.lang.String name,
    se_tpb_speechgen2.external.win.sapi5.SpeechRuleState state);


  /**
   * <p>
   * CmdSetRuleIdState
   * </p>
   * @param ruleId Mandatory int parameter.
   * @param state Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechRuleState parameter.
   */

  @DISPID(13) //= 0xd. The runtime will prefer the VTID if present
  @VTID(19)
  void cmdSetRuleIdState(
    int ruleId,
    se_tpb_speechgen2.external.win.sapi5.SpeechRuleState state);


  /**
   * <p>
   * DictationLoad
   * </p>
   * @param topicName Optional parameter. Default value is ""
   * @param loadOption Optional parameter. Default value is 0
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(20)
  void dictationLoad(
    @Optional @DefaultValue("") java.lang.String topicName,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechLoadOption loadOption);


  /**
   * <p>
   * DictationUnload
   * </p>
   */

  @DISPID(15) //= 0xf. The runtime will prefer the VTID if present
  @VTID(21)
  void dictationUnload();


  /**
   * <p>
   * DictationSetState
   * </p>
   * @param state Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechRuleState parameter.
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(22)
  void dictationSetState(
    se_tpb_speechgen2.external.win.sapi5.SpeechRuleState state);


  /**
   * <p>
   * SetWordSequenceData
   * </p>
   * @param text Mandatory java.lang.String parameter.
   * @param textLength Mandatory int parameter.
   * @param info Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechTextSelectionInformation parameter.
   */

  @DISPID(17) //= 0x11. The runtime will prefer the VTID if present
  @VTID(23)
  void setWordSequenceData(
    java.lang.String text,
    int textLength,
    se_tpb_speechgen2.external.win.sapi5.ISpeechTextSelectionInformation info);


  /**
   * <p>
   * SetTextSelection
   * </p>
   * @param info Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechTextSelectionInformation parameter.
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(24)
  void setTextSelection(
    se_tpb_speechgen2.external.win.sapi5.ISpeechTextSelectionInformation info);


  /**
   * <p>
   * IsPronounceable
   * </p>
   * @param word Mandatory java.lang.String parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechWordPronounceable
   */

  @DISPID(19) //= 0x13. The runtime will prefer the VTID if present
  @VTID(25)
  se_tpb_speechgen2.external.win.sapi5.SpeechWordPronounceable isPronounceable(
    java.lang.String word);


  // Properties:
}
