package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpMMSysAudio Interface
 */
@IID("{15806F6E-1D70-4B48-98E6-3B1A007509AB}")
public interface ISpMMSysAudio extends se_tpb_speechgen2.external.win.sapi5.ISpAudio {
  // Methods:
  /**
   * @return  Returns a value of type int
   */

  @VTID(26)
  int getDeviceId();


  /**
   * @param uDeviceId Mandatory int parameter.
   */

  @VTID(27)
  void setDeviceId(
    int uDeviceId);


  /**
   * @return  Returns a value of type java.nio.Buffer
   */

  @VTID(28)
  java.nio.Buffer getMMHandle();


  /**
   * @return  Returns a value of type int
   */

  @VTID(29)
  int getLineId();


  /**
   * @param uLineId Mandatory int parameter.
   */

  @VTID(30)
  void setLineId(
    int uLineId);


  // Properties:
}
