package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechBaseStream Interface
 */
@IID("{6450336F-7D49-4CED-8097-49D6DEE37294}")
public interface ISpeechBaseStream extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Format
   * </p>
   * <p>
   * Getter method for the COM property "Format"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat format();


  /**
   * <p>
   * Format
   * </p>
   * <p>
   * Setter method for the COM property "Format"
   * </p>
   * @param audioFormat Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  void format(
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat audioFormat);


  /**
   * <p>
   * Read
   * </p>
   * @param buffer Mandatory java.lang.Object parameter.
   * @param numberOfBytes Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  int read(
    java.lang.Object buffer,
    int numberOfBytes);


  /**
   * <p>
   * Write
   * </p>
   * @param buffer Mandatory java.lang.Object parameter.
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(10)
  int write(
    @MarshalAs(NativeType.VARIANT) java.lang.Object buffer);


  /**
   * <p>
   * Seek
   * </p>
   * @param position Mandatory java.lang.Object parameter.
   * @param origin Optional parameter. Default value is 0
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(11)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object seek(
    @MarshalAs(NativeType.VARIANT) java.lang.Object position,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechStreamSeekPositionType origin);


  // Properties:
}
