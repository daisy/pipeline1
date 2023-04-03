package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpNotifyTranslator Interface
 */
@IID("{ACA16614-5D3D-11D2-960E-00C04F8EE628}")
public interface ISpNotifyTranslator extends se_tpb_speechgen2.external.win.sapi5.onecore.ISpNotifySink {
  // Methods:
  /**
   * @param hWnd Mandatory int parameter.
   * @param msg Mandatory int parameter.
   * @param wParam Mandatory long parameter.
   * @param lParam Mandatory long parameter.
   */

  @VTID(4)
  void initWindowMessage(
    int hWnd,
    int msg,
    long wParam,
    long lParam);


  /**
   * @param pfnCallback Mandatory Holder&lt;java.nio.Buffer&gt; parameter.
   * @param wParam Mandatory long parameter.
   * @param lParam Mandatory long parameter.
   */

  @VTID(5)
  void initCallback(
    Holder<java.nio.Buffer> pfnCallback,
    long wParam,
    long lParam);


  /**
   * @param pSpCallback Mandatory Holder&lt;java.nio.Buffer&gt; parameter.
   * @param wParam Mandatory long parameter.
   * @param lParam Mandatory long parameter.
   */

  @VTID(6)
  void initSpNotifyCallback(
    Holder<java.nio.Buffer> pSpCallback,
    long wParam,
    long lParam);


  /**
   * @param hEvent Mandatory java.nio.Buffer parameter.
   * @param fCloseHandleOnRelease Mandatory int parameter.
   */

  @VTID(7)
  void initWin32Event(
    java.nio.Buffer hEvent,
    int fCloseHandleOnRelease);


  /**
   * @param dwMilliseconds Mandatory int parameter.
   */

  @VTID(8)
  void wait_(
    int dwMilliseconds);


  /**
   */

  @VTID(9)
  void getEventHandle();


  // Properties:
}
