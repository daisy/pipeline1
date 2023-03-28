package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpShortcut Interface
 */
@IID("{3DF681E2-EA56-11D9-8BDE-F66BAD1E3F3A}")
public interface ISpShortcut extends Com4jObject {
  // Methods:
  /**
   * @param pszDisplay Mandatory java.lang.String parameter.
   * @param langID Mandatory short parameter.
   * @param pszSpoken Mandatory java.lang.String parameter.
   * @param shType Mandatory se_tpb_speechgen2.external.win.sapi5.onecore.SPSHORTCUTTYPE parameter.
   */

  @VTID(3)
  void addShortcut(
    @MarshalAs(NativeType.Unicode) java.lang.String pszDisplay,
    short langID,
    @MarshalAs(NativeType.Unicode) java.lang.String pszSpoken,
    se_tpb_speechgen2.external.win.sapi5.onecore.SPSHORTCUTTYPE shType);


  /**
   * @param pszDisplay Mandatory java.lang.String parameter.
   * @param langID Mandatory short parameter.
   * @param pszSpoken Mandatory java.lang.String parameter.
   * @param shType Mandatory se_tpb_speechgen2.external.win.sapi5.onecore.SPSHORTCUTTYPE parameter.
   */

  @VTID(4)
  void removeShortcut(
    @MarshalAs(NativeType.Unicode) java.lang.String pszDisplay,
    short langID,
    @MarshalAs(NativeType.Unicode) java.lang.String pszSpoken,
    se_tpb_speechgen2.external.win.sapi5.onecore.SPSHORTCUTTYPE shType);


    /**
     * @param pdwGeneration Mandatory Holder&lt;Integer&gt; parameter.
     */

    @VTID(6)
    void getGeneration(
      Holder<Integer> pdwGeneration);


            // Properties:
          }
