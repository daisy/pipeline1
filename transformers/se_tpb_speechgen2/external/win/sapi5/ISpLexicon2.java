package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpLexicon2 Interface
 */
@IID("{1DCFC449-4F88-4B5D-B06D-2161A5FF4A56}")
public interface ISpLexicon2 extends se_tpb_speechgen2.external.win.sapi5.ISpLexicon {
  // Methods:
  /**
   * @param pszFileName Mandatory java.lang.String parameter.
   * @param dwReserved Mandatory int parameter.
   */

  @VTID(9)
  void loadLexiconFromFile(
    @MarshalAs(NativeType.Unicode) java.lang.String pszFileName,
    int dwReserved);


  /**
   * @param pStream Mandatory se_tpb_speechgen2.external.win.sapi5.IStream parameter.
   * @param dwReserved Mandatory int parameter.
   */

  @VTID(10)
  void loadLexiconFromStream(
    se_tpb_speechgen2.external.win.sapi5.IStream pStream,
    int dwReserved);


  /**
   * @param pszFileName Mandatory java.lang.String parameter.
   * @param dwReserved Mandatory int parameter.
   */

  @VTID(11)
  void saveLexiconToFile(
    @MarshalAs(NativeType.Unicode) java.lang.String pszFileName,
    int dwReserved);


  /**
   * @param pStream Mandatory se_tpb_speechgen2.external.win.sapi5.IStream parameter.
   * @param dwReserved Mandatory int parameter.
   */

  @VTID(12)
  void saveLexiconToStream(
    se_tpb_speechgen2.external.win.sapi5.IStream pStream,
    int dwReserved);


  // Properties:
}
