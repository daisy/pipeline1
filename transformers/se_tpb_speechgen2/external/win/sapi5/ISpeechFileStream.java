package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechFileStream Interface
 */
@IID("{AF67F125-AB39-4E93-B4A2-CC2E66E182A7}")
public interface ISpeechFileStream extends se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream {
  // Methods:
  /**
   * <p>
   * Open
   * </p>
   * @param fileName Mandatory java.lang.String parameter.
   * @param fileMode Optional parameter. Default value is 0
   * @param doEvents Optional parameter. Default value is false
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(12)
  void open(
    java.lang.String fileName,
    @Optional @DefaultValue("0") se_tpb_speechgen2.external.win.sapi5.SpeechStreamFileMode fileMode,
    @Optional @DefaultValue("0") boolean doEvents);


  /**
   * <p>
   * Close
   * </p>
   */

  @DISPID(101) //= 0x65. The runtime will prefer the VTID if present
  @VTID(13)
  void close();


  // Properties:
}
