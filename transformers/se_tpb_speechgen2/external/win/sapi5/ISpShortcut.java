package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpShortcut Interface
 */
@IID("{3DF681E2-EA56-11D9-8BDE-F66BAD1E3F3A}")
public interface ISpShortcut extends Com4jObject {
  // Methods:
  /**
   * @param pszDisplay Mandatory java.lang.String parameter.
   * @param langId Mandatory short parameter.
   * @param pszSpoken Mandatory java.lang.String parameter.
   * @param shType Mandatory se_tpb_speechgen2.external.win.sapi5.SPSHORTCUTTYPE parameter.
   */

  @VTID(3)
  void addShortcut(
    @MarshalAs(NativeType.Unicode) java.lang.String pszDisplay,
    short langId,
    @MarshalAs(NativeType.Unicode) java.lang.String pszSpoken,
    se_tpb_speechgen2.external.win.sapi5.SPSHORTCUTTYPE shType);


  /**
   * @param pszDisplay Mandatory java.lang.String parameter.
   * @param langId Mandatory short parameter.
   * @param pszSpoken Mandatory java.lang.String parameter.
   * @param shType Mandatory se_tpb_speechgen2.external.win.sapi5.SPSHORTCUTTYPE parameter.
   */

  @VTID(4)
  void removeShortcut(
    @MarshalAs(NativeType.Unicode) java.lang.String pszDisplay,
    short langId,
    @MarshalAs(NativeType.Unicode) java.lang.String pszSpoken,
    se_tpb_speechgen2.external.win.sapi5.SPSHORTCUTTYPE shType);


    /**
     * @return  Returns a value of type int
     */

    @VTID(6)
    int getGeneration();


            // Properties:
          }
