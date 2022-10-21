package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpRecognizer2 Interface
 */
@IID("{8FC6D974-C81E-4098-93C5-0147F61ED4D3}")
public interface ISpRecognizer2 extends Com4jObject {
  // Methods:
  /**
   * @param pPhrase Mandatory se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhrase parameter.
   * @param dwCompareFlags Mandatory int parameter.
   */

  @VTID(3)
  void emulateRecognitionEx(
    se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhrase pPhrase,
    int dwCompareFlags);


  /**
   * @param fDoingTraining Mandatory int parameter.
   * @param fAdaptFromTrainingData Mandatory int parameter.
   */

  @VTID(4)
  void setTrainingState(
    int fDoingTraining,
    int fAdaptFromTrainingData);


  /**
   */

  @VTID(5)
  void resetAcousticModelAdaptation();


  // Properties:
}
