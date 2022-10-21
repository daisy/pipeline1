package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecoResultTimes Interface
 */
@IID("{62B3B8FB-F6E7-41BE-BDCB-056B1C29EFC0}")
public interface ISpeechRecoResultTimes extends Com4jObject {
  // Methods:
  /**
   * <p>
   * StreamTime
   * </p>
   * <p>
   * Getter method for the COM property "StreamTime"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object streamTime();


  /**
   * <p>
   * Length
   * </p>
   * <p>
   * Getter method for the COM property "Length"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object length();


  /**
   * <p>
   * TickCount
   * </p>
   * <p>
   * Getter method for the COM property "TickCount"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  int tickCount();


  /**
   * <p>
   * Start
   * </p>
   * <p>
   * Getter method for the COM property "OffsetFromStart"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object offsetFromStart();


  // Properties:
}
