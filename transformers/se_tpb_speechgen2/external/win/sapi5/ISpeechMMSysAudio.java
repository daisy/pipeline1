package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechMMSysAudio Interface
 */
@IID("{3C76AF6D-1FD7-4831-81D1-3B71D5A13C44}")
public interface ISpeechMMSysAudio extends se_tpb_speechgen2.external.win.sapi5.ISpeechAudio {
  // Methods:
  /**
   * <p>
   * DeviceId
   * </p>
   * <p>
   * Getter method for the COM property "DeviceId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(300) //= 0x12c. The runtime will prefer the VTID if present
  @VTID(21)
  int deviceId();


  /**
   * <p>
   * DeviceId
   * </p>
   * <p>
   * Setter method for the COM property "DeviceId"
   * </p>
   * @param deviceId Mandatory int parameter.
   */

  @DISPID(300) //= 0x12c. The runtime will prefer the VTID if present
  @VTID(22)
  void deviceId(
    int deviceId);


  /**
   * <p>
   * LineId
   * </p>
   * <p>
   * Getter method for the COM property "LineId"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(301) //= 0x12d. The runtime will prefer the VTID if present
  @VTID(23)
  int lineId();


  /**
   * <p>
   * LineId
   * </p>
   * <p>
   * Setter method for the COM property "LineId"
   * </p>
   * @param lineId Mandatory int parameter.
   */

  @DISPID(301) //= 0x12d. The runtime will prefer the VTID if present
  @VTID(24)
  void lineId(
    int lineId);


  /**
   * <p>
   * MMHandle
   * </p>
   * <p>
   * Getter method for the COM property "MMHandle"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(302) //= 0x12e. The runtime will prefer the VTID if present
  @VTID(25)
  int mmHandle();


  // Properties:
}
