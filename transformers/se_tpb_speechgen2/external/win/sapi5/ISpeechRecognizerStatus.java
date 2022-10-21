package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecognizerStatus Interface
 */
@IID("{BFF9E781-53EC-484E-BB8A-0E1B5551E35C}")
public interface ISpeechRecognizerStatus extends Com4jObject {
  // Methods:
  /**
   * <p>
   * AudioStatus
   * </p>
   * <p>
   * Getter method for the COM property "AudioStatus"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechAudioStatus
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.ISpeechAudioStatus audioStatus();


  /**
   * <p>
   * CurrentStreamPosition
   * </p>
   * <p>
   * Getter method for the COM property "CurrentStreamPosition"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object currentStreamPosition();


  /**
   * <p>
   * CurrentStreamNumber
   * </p>
   * <p>
   * Getter method for the COM property "CurrentStreamNumber"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  int currentStreamNumber();


  /**
   * <p>
   * NumberOfActiveRules
   * </p>
   * <p>
   * Getter method for the COM property "NumberOfActiveRules"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  int numberOfActiveRules();


  /**
   * <p>
   * ClsidEngine
   * </p>
   * <p>
   * Getter method for the COM property "ClsidEngine"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  java.lang.String clsidEngine();


  /**
   * <p>
   * SupportedLanguages
   * </p>
   * <p>
   * Getter method for the COM property "SupportedLanguages"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object supportedLanguages();


  // Properties:
}
