package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpSerializeState Interface
 */
@IID("{21B501A0-0EC7-46C9-92C3-A2BC784C54B9}")
public interface ISpSerializeState extends Com4jObject {
  // Methods:
    /**
     * @param pbData Mandatory Holder<Byte> parameter.
     * @param ulSize Mandatory int parameter.
     * @param dwReserved Mandatory int parameter.
     */

    @VTID(4)
    void setSerializedState(
      Holder<Byte> pbData,
      int ulSize,
      int dwReserved);


    // Properties:
  }
