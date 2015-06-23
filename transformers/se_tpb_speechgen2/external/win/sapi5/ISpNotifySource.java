package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpNotifySource Interface
 */
@IID("{5EFF4AEF-8487-11D2-961C-00C04F8EE628}")
public interface ISpNotifySource extends Com4jObject {
    @VTID(3)
    void setNotifySink(
        se_tpb_speechgen2.external.win.sapi5.ISpNotifySink pNotifySink);

    @VTID(4)
    void setNotifyWindowMessage(
        int hWnd,
        int msg,
        int wParam,
        int lParam);

    @VTID(5)
    void setNotifyCallbackFunction(
        Holder<java.nio.Buffer> pfnCallback,
        int wParam,
        int lParam);

    @VTID(6)
    void setNotifyCallbackInterface(
        Holder<java.nio.Buffer> pSpCallback,
        int wParam,
        int lParam);

    @VTID(7)
    void setNotifyWin32Event();

    @VTID(8)
    void waitForNotifyEvent(
        int dwMilliseconds);

    @VTID(9)
    void getNotifyEventHandle();

}
