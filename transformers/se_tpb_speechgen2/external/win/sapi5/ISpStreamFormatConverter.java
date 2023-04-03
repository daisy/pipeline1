package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpStreamFormatConverter Interface
 */
@IID("{678A932C-EA71-4446-9B41-78FDA6280A29}")
public interface ISpStreamFormatConverter extends se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat {
  // Methods:
  /**
   * @param pStream Mandatory se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat parameter.
   * @param fSetFormatToBaseStreamFormat Mandatory int parameter.
   * @param fWriteToBaseStream Mandatory int parameter.
   */

  @VTID(15)
  void setBaseStream(
    se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat pStream,
    int fSetFormatToBaseStreamFormat,
    int fWriteToBaseStream);


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat
   */

  @VTID(16)
  se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat getBaseStream();


    /**
     */

    @VTID(18)
    void resetSeekPosition();


    /**
     * @param ullOffsetConvertedStream Mandatory long parameter.
     * @return  Returns a value of type long
     */

    @VTID(19)
    long scaleConvertedToBaseOffset(
      long ullOffsetConvertedStream);


    /**
     * @param ullOffsetBaseStream Mandatory long parameter.
     * @return  Returns a value of type long
     */

    @VTID(20)
    long scaleBaseToConvertedOffset(
      long ullOffsetBaseStream);


    // Properties:
  }
