package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechCustomStream Interface
 */
@IID("{1A9E9F4F-104F-4DB8-A115-EFD7FD0C97AE}")
public interface ISpeechCustomStream extends se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream {
  // Methods:
  /**
   * <p>
   * BaseStream
   * </p>
   * <p>
   * Getter method for the COM property "BaseStream"
   * </p>
   * @return  Returns a value of type com4j.Com4jObject
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(12)
  com4j.Com4jObject baseStream();


  /**
   * <p>
   * BaseStream
   * </p>
   * <p>
   * Setter method for the COM property "BaseStream"
   * </p>
   * @param ppUnkStream Mandatory com4j.Com4jObject parameter.
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(13)
  void baseStream(
    com4j.Com4jObject ppUnkStream);


  // Properties:
}
