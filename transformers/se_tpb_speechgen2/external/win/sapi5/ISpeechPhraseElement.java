package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseElement Interface
 */
@IID("{E6176F96-E373-4801-B223-3B62C068C0B4}")
public interface ISpeechPhraseElement extends Com4jObject {
    /**
     * AudioTimeOffset
     */
    @VTID(7)
    int audioTimeOffset();

    /**
     * AudioSizeTime
     */
    @VTID(8)
    int audioSizeTime();

    /**
     * AudioStreamOffset
     */
    @VTID(9)
    int audioStreamOffset();

    /**
     * AudioSizeBytes
     */
    @VTID(10)
    int audioSizeBytes();

    /**
     * RetainedStreamOffset
     */
    @VTID(11)
    int retainedStreamOffset();

    /**
     * RetainedSizeBytes
     */
    @VTID(12)
    int retainedSizeBytes();

    /**
     * DisplayText
     */
    @VTID(13)
    java.lang.String displayText();

    /**
     * LexicalForm
     */
    @VTID(14)
    java.lang.String lexicalForm();

    /**
     * Pronunciation
     */
    @VTID(15)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object pronunciation();

    /**
     * DisplayAttributes
     */
    @VTID(16)
    se_tpb_speechgen2.external.win.sapi5.SpeechDisplayAttributes displayAttributes();

    /**
     * RequiredConfidence
     */
    @VTID(17)
    se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence requiredConfidence();

    /**
     * ActualConfidence
     */
    @VTID(18)
    se_tpb_speechgen2.external.win.sapi5.SpeechEngineConfidence actualConfidence();

    /**
     * EngineConfidence
     */
    @VTID(19)
    float engineConfidence();

}
