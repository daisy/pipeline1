package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpRecognizer3 Interface
 */
@IID("{DF1B943C-5838-4AA2-8706-D7CD5B333499}")
public interface ISpRecognizer3 extends Com4jObject {
  // Methods:
  /**
   * @param categoryType Mandatory se_tpb_speechgen2.external.win.sapi5.onecore.SPCATEGORYTYPE parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoCategory
   */

  @VTID(3)
  se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoCategory getCategory(
    se_tpb_speechgen2.external.win.sapi5.onecore.SPCATEGORYTYPE categoryType);


  /**
   * @param pCategory Mandatory se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoCategory parameter.
   */

  @VTID(4)
  void setActiveCategory(
    se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoCategory pCategory);


  /**
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoCategory
   */

  @VTID(5)
  se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoCategory getActiveCategory();


  // Properties:
}
