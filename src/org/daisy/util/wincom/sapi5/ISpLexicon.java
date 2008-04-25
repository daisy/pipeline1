package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpLexicon Interface
 */
@IID("{DA41A7C2-5383-4DB2-916B-6C1719E3DB58}")
public interface ISpLexicon extends Com4jObject {
        @VTID(4)
        void addPronunciation(
            @MarshalAs(NativeType.Unicode) java.lang.String pszWord,
            short langId,
            org.daisy.util.wincom.sapi5.SPPARTOFSPEECH ePartOfSpeech,
            @MarshalAs(NativeType.Unicode) java.lang.String pszPronunciation);

        @VTID(5)
        void removePronunciation(
            @MarshalAs(NativeType.Unicode) java.lang.String pszWord,
            short langId,
            org.daisy.util.wincom.sapi5.SPPARTOFSPEECH ePartOfSpeech,
            @MarshalAs(NativeType.Unicode) java.lang.String pszPronunciation);

        @VTID(6)
        void getGeneration(
            Holder<Integer> pdwGeneration);

            }
