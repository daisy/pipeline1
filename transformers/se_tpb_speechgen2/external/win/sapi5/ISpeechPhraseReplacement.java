package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseReplacement Interface
 */
@IID("{2890A410-53A7-4FB5-94EC-06D4998E3D02}")
public interface ISpeechPhraseReplacement extends Com4jObject {
  // Methods:
  /**
   * <p>
   * DisplayAttributes
   * </p>
   * <p>
   * Getter method for the COM property "DisplayAttributes"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes displayAttributes();


  /**
   * <p>
   * Text
   * </p>
   * <p>
   * Getter method for the COM property "Text"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String text();


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


  // Properties:
}
