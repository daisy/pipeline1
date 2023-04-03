package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecognizer Interface
 */
@IID("{C2B5F241-DAA0-4507-9E16-5A1EAA2B7A5C}")
public interface ISpRecognizer extends se_tpb_speechgen2.external.win.sapi5.ISpProperties {
  // Methods:
  /**
   * @param pRecognizer Mandatory se_tpb_speechgen2.external.win.sapi5.ISpObjectToken parameter.
   */

  @VTID(7)
  void setRecognizer(
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken pRecognizer);


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpObjectToken
   */

  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getRecognizer();


  /**
   * @param pUnkInput Mandatory com4j.Com4jObject parameter.
   * @param fAllowFormatChanges Mandatory int parameter.
   */

  @VTID(9)
  void setInput(
    com4j.Com4jObject pUnkInput,
    int fAllowFormatChanges);


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpObjectToken
   */

  @VTID(10)
  se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getInputObjectToken();


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat
   */

  @VTID(11)
  se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat getInputStream();


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpRecoContext
   */

  @VTID(12)
  se_tpb_speechgen2.external.win.sapi5.ISpRecoContext createRecoContext();


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpObjectToken
   */

  @VTID(13)
  se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getRecoProfile();


  /**
   * @param pToken Mandatory se_tpb_speechgen2.external.win.sapi5.ISpObjectToken parameter.
   */

  @VTID(14)
  void setRecoProfile(
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken pToken);


  /**
   */

  @VTID(15)
  void isSharedInstance();


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SPRECOSTATE
   */

  @VTID(16)
  se_tpb_speechgen2.external.win.sapi5.SPRECOSTATE getRecoState();


  /**
   * @param newState Mandatory se_tpb_speechgen2.external.win.sapi5.SPRECOSTATE parameter.
   */

  @VTID(17)
  void setRecoState(
    se_tpb_speechgen2.external.win.sapi5.SPRECOSTATE newState);


    /**
     * @param pszTypeOfUI Mandatory java.lang.String parameter.
     * @param pvExtraData Mandatory java.nio.Buffer parameter.
     * @param cbExtraData Mandatory int parameter.
     * @return  Returns a value of type int
     */

    @VTID(20)
    int isUISupported(
      @MarshalAs(NativeType.Unicode) java.lang.String pszTypeOfUI,
      java.nio.Buffer pvExtraData,
      int cbExtraData);


    /**
     * @param hWndParent Mandatory int parameter.
     * @param pszTitle Mandatory java.lang.String parameter.
     * @param pszTypeOfUI Mandatory java.lang.String parameter.
     * @param pvExtraData Mandatory java.nio.Buffer parameter.
     * @param cbExtraData Mandatory int parameter.
     */

    @VTID(21)
    void displayUI(
      int hWndParent,
      @MarshalAs(NativeType.Unicode) java.lang.String pszTitle,
      @MarshalAs(NativeType.Unicode) java.lang.String pszTypeOfUI,
      java.nio.Buffer pvExtraData,
      int cbExtraData);


    /**
     * @param pPhrase Mandatory se_tpb_speechgen2.external.win.sapi5.ISpPhrase parameter.
     */

    @VTID(22)
    void emulateRecognition(
      se_tpb_speechgen2.external.win.sapi5.ISpPhrase pPhrase);


    // Properties:
  }
