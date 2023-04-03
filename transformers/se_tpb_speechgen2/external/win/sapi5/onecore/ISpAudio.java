package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpAudio Interface
 */
@IID("{F4C6587C-C49E-4254-923B-FB0B09FE13C7}")
public interface ISpAudio extends se_tpb_speechgen2.external.win.sapi5.onecore.ISpStreamFormat {
  // Methods:
  /**
   * @param newState Mandatory se_tpb_speechgen2.external.win.sapi5.onecore.SPAUDIOSTATE parameter.
   * @param ullReserved Mandatory long parameter.
   */

  @VTID(15)
  void setState(
    se_tpb_speechgen2.external.win.sapi5.onecore.SPAUDIOSTATE newState,
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
