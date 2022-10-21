package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecoContext2 Interface
 */
@IID("{BEAD311C-52FF-437F-9464-6B21054CA73D}")
public interface ISpRecoContext2 extends Com4jObject {
  // Methods:
  /**
   * @param eGrammarOptions Mandatory int parameter.
   */

  @VTID(3)
  void setGrammarOptions(
    int eGrammarOptions);


  /**
   * @return  Returns a value of type int
   */

  @VTID(4)
  int getGrammarOptions();


  /**
   * @param pAdaptationData Mandatory java.lang.String parameter.
   * @param cch Mandatory int parameter.
   * @param pTopicName Mandatory java.lang.String parameter.
   * @param eAdaptationSettings Mandatory int parameter.
   * @param eRelevance Mandatory se_tpb_speechgen2.external.win.sapi5.SPADAPTATIONRELEVANCE parameter.
   */

  @VTID(5)
  void setAdaptationData2(
    @MarshalAs(NativeType.Unicode) java.lang.String pAdaptationData,
    int cch,
    @MarshalAs(NativeType.Unicode) java.lang.String pTopicName,
    int eAdaptationSettings,
    se_tpb_speechgen2.external.win.sapi5.SPADAPTATIONRELEVANCE eRelevance);


  // Properties:
}
