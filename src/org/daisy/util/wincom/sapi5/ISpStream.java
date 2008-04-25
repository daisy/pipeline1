package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpStream Interface
 */
@IID("{12E3CCA9-7518-44C5-A5E7-BA5A79CB929E}")
public interface ISpStream extends org.daisy.util.wincom.sapi5.ISpStreamFormat {
        @VTID(16)
        void getBaseStream(
            Holder<org.daisy.util.wincom.sapi5.IStream> ppStream);

            @VTID(18)
            void close();

        }
