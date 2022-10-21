package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechWaveFormatEx Interface
 */
@IID("{7A1EF0D5-1581-4741-88E4-209A49F11A10}")
public interface ISpeechWaveFormatEx extends Com4jObject {
  // Methods:
  /**
   * <p>
   * FormatTag
   * </p>
   * <p>
   * Getter method for the COM property "FormatTag"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  short formatTag();


  /**
   * <p>
   * FormatTag
   * </p>
   * <p>
   * Setter method for the COM property "FormatTag"
   * </p>
   * @param formatTag Mandatory short parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  void formatTag(
    short formatTag);


  /**
   * <p>
   * Channels
   * </p>
   * <p>
   * Getter method for the COM property "Channels"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  short channels();


  /**
   * <p>
   * Channels
   * </p>
   * <p>
   * Setter method for the COM property "Channels"
   * </p>
   * @param channels Mandatory short parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  void channels(
    short channels);


  /**
   * <p>
   * SamplesPerSec
   * </p>
   * <p>
   * Getter method for the COM property "SamplesPerSec"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(11)
  int samplesPerSec();


  /**
   * <p>
   * SamplesPerSec
   * </p>
   * <p>
   * Setter method for the COM property "SamplesPerSec"
   * </p>
   * @param samplesPerSec Mandatory int parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(12)
  void samplesPerSec(
    int samplesPerSec);


  /**
   * <p>
   * AvgBytesPerSec
   * </p>
   * <p>
   * Getter method for the COM property "AvgBytesPerSec"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(13)
  int avgBytesPerSec();


  /**
   * <p>
   * AvgBytesPerSec
   * </p>
   * <p>
   * Setter method for the COM property "AvgBytesPerSec"
   * </p>
   * @param avgBytesPerSec Mandatory int parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(14)
  void avgBytesPerSec(
    int avgBytesPerSec);


  /**
   * <p>
   * BlockAlign
   * </p>
   * <p>
   * Getter method for the COM property "BlockAlign"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(15)
  short blockAlign();


  /**
   * <p>
   * BlockAlign
   * </p>
   * <p>
   * Setter method for the COM property "BlockAlign"
   * </p>
   * @param blockAlign Mandatory short parameter.
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(16)
  void blockAlign(
    short blockAlign);


  /**
   * <p>
   * BitsPerSample
   * </p>
   * <p>
   * Getter method for the COM property "BitsPerSample"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(17)
  short bitsPerSample();


  /**
   * <p>
   * BitsPerSample
   * </p>
   * <p>
   * Setter method for the COM property "BitsPerSample"
   * </p>
   * @param bitsPerSample Mandatory short parameter.
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(18)
  void bitsPerSample(
    short bitsPerSample);


  /**
   * <p>
   * ExtraData
   * </p>
   * <p>
   * Getter method for the COM property "ExtraData"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(19)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object extraData();


  /**
   * <p>
   * ExtraData
   * </p>
   * <p>
   * Setter method for the COM property "ExtraData"
   * </p>
   * @param extraData Mandatory java.lang.Object parameter.
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(20)
  void extraData(
    @MarshalAs(NativeType.VARIANT) java.lang.Object extraData);


  // Properties:
}
