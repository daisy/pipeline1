package org.daisy.util.wincom.sapi5  ;

import com4j.*;

@IID("{0000000C-0000-0000-C000-000000000046}")
public interface IStream extends org.daisy.util.wincom.sapi5.ISequentialStream {
            @VTID(8)
            void commit(
                int grfCommitFlags);

            @VTID(9)
            void revert();

                    @VTID(13)
                    org.daisy.util.wincom.sapi5.IStream clone();

                }
