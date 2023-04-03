package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseProperty Interface
 */
@IID("{CE563D48-961E-4732-A2E1-378A42B430BE}")
public interface ISpeechPhraseProperty extends Com4jObject {
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
   * Value
   * </p>
   * <p>
   * Getter method for the COM property "Value"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object value();


  /**
   * <p>
   * FirstElement
   * </p>
   * <p>
   * Getter method for the COM property "FirstElement"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  int firstElement();


  /**
   * <p>
   * NumberOfElements
   * </p>
   * <p>
   * Getter method for the COM property "NumberOfElements"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  int numberOfElements();


  /**
   * <p>
   * EngineConfidence
   * </p>
   * <p>
   * Getter method for the COM property "EngineConfidence"
   * </p>
   * @return  Returns a value of type float
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  float engineConfidence();


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
   * Parent
   * </p>
   * <p>
   * Getter method for the COM property "Parent"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperty
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperty parent();


  /**
   * <p>
   * Children
   * </p>
   * <p>
   * Getter method for the COM property "Children"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperties
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperties children();


  @VTID(15)
  @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperties.class})
  se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseProperty children(
    int index);

  // Properties:
}
