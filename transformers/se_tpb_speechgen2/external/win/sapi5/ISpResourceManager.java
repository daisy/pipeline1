package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpResourceManager Interface
 */
@IID("{93384E18-5014-43D5-ADBB-A78E055926BD}")
public interface ISpResourceManager extends se_tpb_speechgen2.external.win.sapi5.IServiceProvider {
    @VTID(4)
    void setObject(
        GUID guidServiceId,
        com4j.Com4jObject punkObject);

    @VTID(5)
    java.nio.Buffer getObject(
        GUID guidServiceId,
        GUID objectCLSID,
        GUID objectIID,
        int fReleaseWhenLastExternalRefReleased);

}
