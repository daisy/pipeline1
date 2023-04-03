package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechVoiceStatus Interface
 */
@IID("{8BE47B07-57F6-11D2-9EEE-00C04F797396}")
public interface ISpeechVoiceStatus extends Com4jObject {
  // Methods:
  /**
   * <p>
   * CurrentStreamNumber
   * </p>
   * <p>
   * Getter method for the COM property "CurrentStreamNumber"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int currentStreamNumber();


  /**
   * <p>
   * LastStreamNumberQueued
   * </p>
   * <p>
   * Getter method for the COM property "LastStreamNumberQueued"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  int lastStreamNumberQueued();


  /**
   * <p>
   * LastHResult
   * </p>
   * <p>
   * Getter method for the COM property "LastHResult"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  int lastHResult();


  /**
   * <p>
   * RunningState
   * </p>
   * <p>
   * Getter method for the COM property "RunningState"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechRunState
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  se_tpb_speechgen2.external.win.sapi5.SpeechRunState runningState();


  /**
   * <p>
   * InputWordPosition
   * </p>
   * <p>
   * Getter method for the COM property "InputWordPosition"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  int inputWordPosition();


  /**
   * <p>
   * InputWordLength
   * </p>
   * <p>
   * Getter method for the COM property "InputWordLength"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  int inputWordLength();


  /**
   * <p>
   * InputSentencePosition
   * </p>
   * <p>
   * Getter method for the COM property "InputSentencePosition"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  int inputSentencePosition();


  /**
   * <p>
   * InputSentenceLength
   * </p>
   * <p>
   * Getter method for the COM property "InputSentenceLength"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  int inputSentenceLength();


  /**
   * <p>
   * LastBookmark
   * </p>
   * <p>
   * Getter method for the COM property "LastBookmark"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  java.lang.String lastBookmark();


  /**
   * <p>
   * LastBookmarkId
   * </p>
   * <p>
   * Getter method for the COM property "LastBookmarkId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(16)
  int lastBookmarkId();


  /**
   * <p>
   * PhonemeId
   * </p>
   * <p>
   * Getter method for the COM property "PhonemeId"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(17)
  short phonemeId();


  /**
   * <p>
   * VisemeId
   * </p>
   * <p>
   * Getter method for the COM property "VisemeId"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(18)
  short visemeId();


  // Properties:
}
