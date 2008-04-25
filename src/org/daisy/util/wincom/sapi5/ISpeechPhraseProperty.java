package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechPhraseProperty Interface
 */
@IID("{CE563D48-961E-4732-A2E1-378A42B430BE}")
public interface ISpeechPhraseProperty extends Com4jObject {
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
     * Value
     */
    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object value();

    /**
     * FirstElement
     */
    @VTID(10)
    int firstElement();

    /**
     * NumberOfElements
     */
    @VTID(11)
    int numberOfElements();

    /**
     * EngineConfidence
     */
    @VTID(12)
    float engineConfidence();

    /**
     * Confidence
     */
    @VTID(13)
    org.daisy.util.wincom.sapi5.SpeechEngineConfidence confidence();

    /**
     * Parent
     */
    @VTID(14)
    org.daisy.util.wincom.sapi5.ISpeechPhraseProperty parent();

    /**
     * Children
     */
    @VTID(15)
    org.daisy.util.wincom.sapi5.ISpeechPhraseProperties children();

    @VTID(15)
    @ReturnValue(defaultPropertyThrough={org.daisy.util.wincom.sapi5.ISpeechPhraseProperties.class})
    org.daisy.util.wincom.sapi5.ISpeechPhraseProperty children(
        int index);

}
