package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

@IID("{0000000C-0000-0000-C000-000000000046}")
public interface IStream extends se_tpb_speechgen2.external.win.sapi5.ISequentialStream {
            @VTID(8)
            void commit(
                int grfCommitFlags);

            @VTID(9)
            void revert();

                    @VTID(13)
                    se_tpb_speechgen2.external.win.sapi5.IStream clone();

                }
