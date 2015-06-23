package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseRule Interface
 */
@IID("{A7BFE112-A4A0-48D9-B602-C313843F6964}")
public interface ISpeechPhraseRule extends Com4jObject {
    /**
     * Name
     */
    @VTID(7)
    java.lang.String name();

    /**
     * Id
     */
    @VTID(8)
    int id();

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

    /**
     * Parent
     */
    @VTID(11)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRule parent();

    /**
     * Children
     */
    @VTID(12)
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRules children();

    @VTID(12)
    @ReturnValue(defaultPropertyThrough={se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRules.class})
    se_tpb_speechgen2.external.win.sapi5.ISpeechPhraseRule children(
        int index);

    /**
     * Confidence
     */
    @VTID(13)
    se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence confidence();

    /**
     * EngineConfidence
     */
    @VTID(14)
    float engineConfidence();

}
