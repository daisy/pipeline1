package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpStream Interface
 */
@IID("{12E3CCA9-7518-44C5-A5E7-BA5A79CB929E}")
public interface ISpStream extends se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat {
  // Methods:
    /**
     * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.IStream
     */

    @VTID(16)
    se_tpb_speechgen2.external.win.sapi5.IStream getBaseStream();


      /**
       */

      @VTID(18)
      void close();


      // Properties:
    }
