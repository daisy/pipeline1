package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpPromptVoice Interface
 */
@IID("{CABE307A-DDD1-4650-A3AA-3A5E8DCE91AF}")
public interface ISpPromptVoice extends Com4jObject {
  // Methods:
  /**
   * @param pToken Mandatory se_tpb_speechgen2.external.win.sapi5.ISpObjectToken parameter.
   * @param dwFlags Mandatory int parameter.
   */

  @VTID(3)
  void setBackupVoice(
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken pToken,
    int dwFlags);


  /**
   * @param pwcsLocalName Mandatory java.lang.String parameter.
   * @param pwcsAlias Mandatory java.lang.String parameter.
   * @param dwFlags Mandatory int parameter.
   */

  @VTID(4)
  void loadDatabase(
    @MarshalAs(NativeType.Unicode) java.lang.String pwcsLocalName,
    @MarshalAs(NativeType.Unicode) java.lang.String pwcsAlias,
    int dwFlags);


  /**
   * @param pwcsAlias Mandatory java.lang.String parameter.
   * @param dwFlags Mandatory int parameter.
   */

  @VTID(5)
  void unloadDatabase(
    @MarshalAs(NativeType.Unicode) java.lang.String pwcsAlias,
    int dwFlags);


  /**
   * @param pLoader Mandatory se_tpb_speechgen2.external.win.sapi5.ISpeechResourceLoader parameter.
   */

  @VTID(6)
  void setResourceLoader(
    se_tpb_speechgen2.external.win.sapi5.ISpeechResourceLoader pLoader);


  // Properties:
}
