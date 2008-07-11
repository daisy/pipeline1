package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpNotifyTranslator Interface
 */
@IID("{ACA16614-5D3D-11D2-960E-00C04F8EE628}")
public interface ISpNotifyTranslator extends se_tpb_speechgen2.external.win.sapi5.ISpNotifySink {
    @VTID(4)
    void initWindowMessage(
        int hWnd,
        int msg,
        int wParam,
        int lParam);

    @VTID(5)
    void initCallback(
        Holder<java.nio.Buffer> pfnCallback,
        int wParam,
        int lParam);

    @VTID(6)
    void initSpNotifyCallback(
        Holder<java.nio.Buffer> pSpCallback,
        int wParam,
        int lParam);

    @VTID(7)
    void initWin32Event(
        java.nio.Buffer hEvent,
        int fCloseHandleOnRelease);

    @VTID(8)
    void wait_(
        int dwMilliseconds);

    @VTID(9)
    void getEventHandle();

}
