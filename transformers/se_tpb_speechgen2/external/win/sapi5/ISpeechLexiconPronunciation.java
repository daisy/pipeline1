package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechLexiconPronunciation Interface
 */
@IID("{95252C5D-9E43-4F4A-9899-48EE73352F9F}")
public interface ISpeechLexiconPronunciation extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Type
   * </p>
   * <p>
   * Getter method for the COM property "Type"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechLexiconType
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.SpeechLexiconType type();


  /**
   * <p>
   * LangId
   * </p>
   * <p>
   * Getter method for the COM property "LangId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  int langId();


  /**
   * <p>
   * PartOfSpeech
   * </p>
   * <p>
   * Getter method for the COM property "PartOfSpeech"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechPartOfSpeech
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  se_tpb_speechgen2.external.win.sapi5.SpeechPartOfSpeech partOfSpeech();


  /**
   * <p>
   * PhoneIds
   * </p>
   * <p>
   * Getter method for the COM property "PhoneIds"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object phoneIds();


  /**
   * <p>
   * Symbolic
   * </p>
   * <p>
   * Getter method for the COM property "Symbolic"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  java.lang.String symbolic();


  // Properties:
}
