package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

@IID("{0C733A30-2A1C-11CE-ADE5-00AA0044773D}")
public interface ISequentialStream extends Com4jObject {
    @VTID(3)
    void remoteRead(
        Holder<Byte> pv,
        int cb,
        Holder<Integer> pcbRead);

    @VTID(4)
    int remoteWrite(
        Holder<Byte> pv,
        int cb);

}
