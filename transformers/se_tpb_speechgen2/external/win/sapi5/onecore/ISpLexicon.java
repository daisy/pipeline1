package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * ISpLexicon Interface
 */
@IID("{DA41A7C2-5383-4DB2-916B-6C1719E3DB58}")
public interface ISpLexicon extends Com4jObject {
  // Methods:
    /**
     * @param pszWord Mandatory java.lang.String parameter.
     * @param langID Mandatory short parameter.
     * @param ePartOfSpeech Mandatory se_tpb_speechgen2.external.win.sapi5.onecore.SPPARTOFSPEECH parameter.
     * @param pszPronunciation Mandatory java.lang.String parameter.
     */

    @VTID(4)
    void addPronunciation(
      @MarshalAs(NativeType.Unicode) java.lang.String pszWord,
      short langID,
      se_tpb_speechgen2.external.win.sapi5.onecore.SPPARTOFSPEECH ePartOfSpeech,
      @MarshalAs(NativeType.Unicode) java.lang.String pszPronunciation);


    /**
     * @param pszWord Mandatory java.lang.String parameter.
     * @param langID Mandatory short parameter.
     * @param ePartOfSpeech Mandatory se_tpb_speechgen2.external.win.sapi5.onecore.SPPARTOFSPEECH parameter.
     * @param pszPronunciation Mandatory java.lang.String parameter.
     */

    @VTID(5)
    void removePronunciation(
      @MarshalAs(NativeType.Unicode) java.lang.String pszWord,
      short langID,
      se_tpb_speechgen2.external.win.sapi5.onecore.SPPARTOFSPEECH ePartOfSpeech,
      @MarshalAs(NativeType.Unicode) java.lang.String pszPronunciation);


    /**
     * @param pdwGeneration Mandatory Holder&lt;Integer&gt; parameter.
     */

    @VTID(6)
    void getGeneration(
      Holder<Integer> pdwGeneration);


        // Properties:
      }
