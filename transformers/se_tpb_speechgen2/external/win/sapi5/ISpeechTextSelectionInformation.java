package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechTextSelectionInformation Interface
 */
@IID("{3B9C7E7A-6EEE-4DED-9092-11657279ADBE}")
public interface ISpeechTextSelectionInformation extends Com4jObject {
  // Methods:
  /**
   * <p>
   * ActiveOffset
   * </p>
   * <p>
   * Setter method for the COM property "ActiveOffset"
   * </p>
   * @param activeOffset Mandatory int parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  void activeOffset(
    int activeOffset);


  /**
   * <p>
   * ActiveOffset
   * </p>
   * <p>
   * Getter method for the COM property "ActiveOffset"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  int activeOffset();


  /**
   * <p>
   * ActiveLength
   * </p>
   * <p>
   * Setter method for the COM property "ActiveLength"
   * </p>
   * @param activeLength Mandatory int parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  void activeLength(
    int activeLength);


  /**
   * <p>
   * ActiveLength
   * </p>
   * <p>
   * Getter method for the COM property "ActiveLength"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  int activeLength();


  /**
   * <p>
   * SelectionOffset
   * </p>
   * <p>
   * Setter method for the COM property "SelectionOffset"
   * </p>
   * @param selectionOffset Mandatory int parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(11)
  void selectionOffset(
    int selectionOffset);


  /**
   * <p>
   * SelectionOffset
   * </p>
   * <p>
   * Getter method for the COM property "SelectionOffset"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(12)
  int selectionOffset();


  /**
   * <p>
   * SelectionLength
   * </p>
   * <p>
   * Setter method for the COM property "SelectionLength"
   * </p>
   * @param selectionLength Mandatory int parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(13)
  void selectionLength(
    int selectionLength);


  /**
   * <p>
   * SelectionLength
   * </p>
   * <p>
   * Getter method for the COM property "SelectionLength"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(14)
  int selectionLength();


  // Properties:
}
