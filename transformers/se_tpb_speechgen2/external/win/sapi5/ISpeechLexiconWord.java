package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechLexiconWord Interface
 */
@IID("{4E5B933C-C9BE-48ED-8842-1EE51BB1D4FF}")
public interface ISpeechLexiconWord extends Com4jObject {
    @VTID(7)
    int langId();

    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.SpeechWordType type();

    @VTID(9)
    java.lang.String word();

    @VTID(10)
    se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciations pronunciations();

    @VTID(10)
    @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciations.class})
    se_tpb_speechgen2.external.win.sapi5.ISpeechLexiconPronunciation pronunciations(
        int index);

}
