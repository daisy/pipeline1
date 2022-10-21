package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseRule Interface
 */
@IID("{A7BFE112-A4A0-48D9-B602-C313843F6964}")
public interface ISpeechPhraseRule extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Name
   * </p>
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String name();


  /**
   * <p>
   * Id
   * </p>
   * <p>
   * Getter method for the COM property "Id"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  int id();


  /**
   * <p>
   * FirstElement
   * </p>
   * <p>
   * Getter method for the COM property "FirstElement"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  int firstElement();


  /**
   * <p>
   * NumElements
   * </p>
   * <p>
   * Getter method for the COM property "NumberOfElements"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  int numberOfElements();


  /**
   * <p>
   * Parent
   * </p>
   * <p>
   * Getter method for the COM property "Parent"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRule
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRule parent();


  /**
   * <p>
   * Children
   * </p>
   * <p>
   * Getter method for the COM property "Children"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRules
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRules children();


  @VTID(12)
  @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRules.class})
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRule children(
    int index);

  /**
   * <p>
   * Confidence
   * </p>
   * <p>
   * Getter method for the COM property "Confidence"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence confidence();


  /**
   * <p>
   * EngineConfidence
   * </p>
   * <p>
   * Getter method for the COM property "EngineConfidence"
   * </p>
   * @return  Returns a value of type float
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  float engineConfidence();


  // Properties:
}
