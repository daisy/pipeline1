package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpAudio Interface
 */
@IID("{C05C768F-FAE8-4EC2-8E07-338321C12452}")
public interface ISpAudio extends se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat {
    @VTID(15)
    void setState(
        se_tpb_speechgen2.external.win.sapi5.SPAUDIOSTATE newState,
        long ullReserved);

                @VTID(21)
                void eventHandle();

                @VTID(22)
                int getVolumeLevel();

                @VTID(23)
                void setVolumeLevel(
                    int level);

                @VTID(24)
                int getBufferNotifySize();

                @VTID(25)
                void setBufferNotifySize(
                    int cbSize);

            }
