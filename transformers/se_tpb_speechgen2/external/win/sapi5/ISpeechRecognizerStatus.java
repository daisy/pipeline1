package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecognizerStatus Interface
 */
@IID("{BFF9E781-53EC-484E-BB8A-0E1B5551E35C}")
public interface ISpeechRecognizerStatus extends Com4jObject {
    /**
     * AudioStatus
     */
    @VTID(7)
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioStatus audioStatus();

    /**
     * CurrentStreamPosition
     */
    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object currentStreamPosition();

    /**
     * CurrentStreamNumber
     */
    @VTID(9)
    int currentStreamNumber();

    /**
     * NumberOfActiveRules
     */
    @VTID(10)
    int numberOfActiveRules();

    /**
     * ClsidEngine
     */
    @VTID(11)
    java.lang.String clsidEngine();

    /**
     * SupportedLanguages
     */
    @VTID(12)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object supportedLanguages();

}
