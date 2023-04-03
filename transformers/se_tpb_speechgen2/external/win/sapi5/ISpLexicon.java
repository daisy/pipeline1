package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpLexicon Interface
 */
@IID("{DA41A7C2-5383-4DB2-916B-6C1719E3DB58}")
public interface ISpLexicon extends Com4jObject {
  // Methods:
    /**
     * @param pszWord Mandatory java.lang.String parameter.
     * @param langId Mandatory short parameter.
     * @param ePartOfSpeech Mandatory se_tpb_speechgen2.external.win.sapi5.SPPARTOFSPEECH parameter.
     * @param pszPronunciation Mandatory java.lang.String parameter.
     */

    @VTID(4)
    void addPronunciation(
      @MarshalAs(NativeType.Unicode) java.lang.String pszWord,
      short langId,
      se_tpb_speechgen2.external.win.sapi5.SPPARTOFSPEECH ePartOfSpeech,
      @MarshalAs(NativeType.Unicode) java.lang.String pszPronunciation);


    /**
     * @param pszWord Mandatory java.lang.String parameter.
     * @param langId Mandatory short parameter.
     * @param ePartOfSpeech Mandatory se_tpb_speechgen2.external.win.sapi5.SPPARTOFSPEECH parameter.
     * @param pszPronunciation Mandatory java.lang.String parameter.
     */

    @VTID(5)
    void removePronunciation(
      @MarshalAs(NativeType.Unicode) java.lang.String pszWord,
      short langId,
      se_tpb_speechgen2.external.win.sapi5.SPPARTOFSPEECH ePartOfSpeech,
      @MarshalAs(NativeType.Unicode) java.lang.String pszPronunciation);


    /**
     * @return  Returns a value of type int
     */

    @VTID(6)
    int getGeneration();


        // Properties:
      }
