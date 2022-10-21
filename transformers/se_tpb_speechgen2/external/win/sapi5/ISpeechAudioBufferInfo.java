package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechAudioBufferInfo Interface
 */
@IID("{11B103D8-1142-4EDF-A093-82FB3915F8CC}")
public interface ISpeechAudioBufferInfo extends Com4jObject {
  // Methods:
  /**
   * <p>
   * MinNotification
   * </p>
   * <p>
   * Getter method for the COM property "MinNotification"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int minNotification();


  /**
   * <p>
   * MinNotification
   * </p>
   * <p>
   * Setter method for the COM property "MinNotification"
   * </p>
   * @param minNotification Mandatory int parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  void minNotification(
    int minNotification);


  /**
   * <p>
   * BufferSize
   * </p>
   * <p>
   * Getter method for the COM property "BufferSize"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  int bufferSize();


  /**
   * <p>
   * BufferSize
   * </p>
   * <p>
   * Setter method for the COM property "BufferSize"
   * </p>
   * @param bufferSize Mandatory int parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  void bufferSize(
    int bufferSize);


  /**
   * <p>
   * EventBias
   * </p>
   * <p>
   * Getter method for the COM property "EventBias"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(11)
  int eventBias();


  /**
   * <p>
   * EventBias
   * </p>
   * <p>
   * Setter method for the COM property "EventBias"
   * </p>
   * @param eventBias Mandatory int parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(12)
  void eventBias(
    int eventBias);


  // Properties:
}
