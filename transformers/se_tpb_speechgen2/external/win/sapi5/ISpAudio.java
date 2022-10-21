package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpAudio Interface
 */
@IID("{C05C768F-FAE8-4EC2-8E07-338321C12452}")
public interface ISpAudio extends se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat {
  // Methods:
  /**
   * @param newState Mandatory se_tpb_speechgen2.external.win.sapi5.SPAUDIOSTATE parameter.
   * @param ullReserved Mandatory long parameter.
   */

  @VTID(15)
  void setState(
    se_tpb_speechgen2.external.win.sapi5.SPAUDIOSTATE newState,
    long ullReserved);


        /**
         */

        @VTID(21)
        void eventHandle();


        /**
         * @return  Returns a value of type int
         */

        @VTID(22)
        int getVolumeLevel();


        /**
         * @param level Mandatory int parameter.
         */

        @VTID(23)
        void setVolumeLevel(
          int level);


        /**
         * @return  Returns a value of type int
         */

        @VTID(24)
        int getBufferNotifySize();


        /**
         * @param cbSize Mandatory int parameter.
         */

        @VTID(25)
        void setBufferNotifySize(
          int cbSize);


        // Properties:
      }
