package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseReplacement Interface
 */
@IID("{2890A410-53A7-4FB5-94EC-06D4998E3D02}")
public interface ISpeechPhraseReplacement extends Com4jObject {
    /**
     * DisplayAttributes
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes displayAttributes();

    /**
     * Text
     */
    @VTID(8)
    java.lang.String text();

    /**
     * FirstElement
     */
    @VTID(9)
    int firstElement();

    /**
     * NumElements
     */
    @VTID(10)
    int numberOfElements();

}
