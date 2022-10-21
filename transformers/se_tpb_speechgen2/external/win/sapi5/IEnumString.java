package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

@IID("{00000101-0000-0000-C000-000000000046}")
public interface IEnumString extends Com4jObject {
  // Methods:
    /**
     * @param celt Mandatory int parameter.
     */

    @VTID(4)
    void skip(
      int celt);


    /**
     */

    @VTID(5)
    void reset();


    /**
     * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.IEnumString
     */

    @VTID(6)
    se_tpb_speechgen2.external.win.sapi5.IEnumString clone();


    // Properties:
  }
