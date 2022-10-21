package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechLexiconWord Interface
 */
@IID("{4E5B933C-C9BE-48ED-8842-1EE51BB1D4FF}")
public interface ISpeechLexiconWord extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "LangId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int langId();


  /**
   * <p>
   * Getter method for the COM property "Type"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechWordType
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.SpeechWordType type();


  /**
   * <p>
   * Getter method for the COM property "Word"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String word();


  /**
   * <p>
   * Getter method for the COM property "Pronunciations"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciations
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciations pronunciations();


  @VTID(10)
  @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciations.class})
  se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciation pronunciations(
    int index);

  // Properties:
}
