package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * IEnumSpObjectTokens Interface
 */
@IID("{06B64F9E-7FDA-11D2-B4F2-00C04F797396}")
public interface IEnumSpObjectTokens extends Com4jObject {
    @VTID(3)
    void next(
        int celt,
        Holder<se_tpb_speechgen2.external.win.sapi5.ISpObjectToken> pelt,
        Holder<Integer> pceltFetched);

    @VTID(4)
    void skip(
        int celt);

    @VTID(5)
    void reset();

    @VTID(6)
    se_tpb_speechgen2.external.win.sapi5.IEnumSpObjectTokens clone();

    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken item(
        int index);

    @VTID(8)
    int getCount();

}
