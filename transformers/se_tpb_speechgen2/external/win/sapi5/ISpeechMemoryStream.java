package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechMemoryStream Interface
 */
@IID("{EEB14B68-808B-4ABE-A5EA-B51DA7588008}")
public interface ISpeechMemoryStream extends se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream {
  // Methods:
  /**
   * <p>
   * SetData
   * </p>
   * @param data Mandatory java.lang.Object parameter.
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(12)
  void setData(
    @MarshalAs(NativeType.VARIANT) java.lang.Object data);


  /**
   * <p>
   * GetData
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(101) //= 0x65. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getData();


  // Properties:
}
