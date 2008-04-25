package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpProperties Interface
 */
@IID("{5B4FB971-B115-4DE1-AD97-E482E3BF6EE4}")
public interface ISpProperties extends Com4jObject {
    @VTID(3)
    void setPropertyNum(
        Holder<Short> pName,
        int lValue);

    @VTID(4)
    int getPropertyNum(
        Holder<Short> pName);

    @VTID(5)
    void setPropertyString(
        Holder<Short> pName,
        Holder<Short> pValue);

    @VTID(6)
    Holder<Short> getPropertyString(
        Holder<Short> pName);

}
