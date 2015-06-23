package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechLexiconPronunciation Interface
 */
@IID("{95252C5D-9E43-4F4A-9899-48EE73352F9F}")
public interface ISpeechLexiconPronunciation extends Com4jObject {
    /**
     * Type
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.SpeechLexiconType type();

    /**
     * LangId
     */
    @VTID(8)
    int langId();

    /**
     * PartOfSpeech
     */
    @VTID(9)
    se_tpb_speechgen2.external.win.sapi5.SpeechPartOfSpeech partOfSpeech();

    /**
     * PhoneIds
     */
    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object phoneIds();

    /**
     * Symbolic
     */
    @VTID(11)
    java.lang.String symbolic();

}
