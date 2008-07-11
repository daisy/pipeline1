package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechRecognizer Interface
 */
@IID("{2D5F1C0C-BD75-4B08-9478-3B11FEA2586C}")
public interface ISpeechRecognizer extends Com4jObject {
    /**
     * Recognizer
     */
    @VTID(7)
    void recognizer(
        se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken recognizer);

    /**
     * Recognizer
     */
    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken recognizer();

    /**
     * AllowAudioInputFormatChangesOnNextSet
     */
    @VTID(9)
    void allowAudioInputFormatChangesOnNextSet(
        boolean allow);

    /**
     * AllowAudioInputFormatChangesOnNextSet
     */
    @VTID(10)
    boolean allowAudioInputFormatChangesOnNextSet();

    /**
     * AudioInput
     */
    @VTID(11)
    void audioInput(
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken audioInput);

    /**
     * AudioInput
     */
    @VTID(12)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken audioInput();

    /**
     * AudioInputStream
     */
    @VTID(13)
    void audioInputStream(
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream audioInputStream);

    /**
     * AudioInputStream
     */
    @VTID(14)
    se_tpb_speechgen2.external.win.sapi5.ISpeechBaseStream audioInputStream();

    /**
     * IsShared
     */
    @VTID(15)
    boolean isShared();

    /**
     * State
     */
    @VTID(16)
    void state(
        se_tpb_speechgen2.external.win.sapi5.SpeechRecognizerState state);

    /**
     * State
     */
    @VTID(17)
    se_tpb_speechgen2.external.win.sapi5.SpeechRecognizerState state();

    /**
     * Status
     */
    @VTID(18)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecognizerStatus status();

    /**
     * Profile
     */
    @VTID(19)
    void profile(
        @DefaultValue("0")se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken profile);

    /**
     * Profile
     */
    @VTID(20)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectToken profile();

    /**
     * EmulateRecognition
     */
    @VTID(21)
    void emulateRecognition(
        @MarshalAs(NativeType.VARIANT) java.lang.Object textElements,
        @DefaultValue("")java.lang.Object elementDisplayAttributes,
        @DefaultValue("0")int languageId);

    /**
     * CreateRecoContext
     */
    @VTID(22)
    se_tpb_speechgen2.external.win.sapi5.ISpeechRecoContext createRecoContext();

    /**
     * GetFormat
     */
    @VTID(23)
    se_tpb_speechgen2.external.win.sapi5.ISpeechAudioFormat getFormat(
        se_tpb_speechgen2.external.win.sapi5.SpeechFormatType type);

    /**
     * SetPropertyNumber
     */
    @VTID(24)
    boolean setPropertyNumber(
        java.lang.String name,
        int value);

    /**
     * GetPropertyNumber
     */
    @VTID(25)
    boolean getPropertyNumber(
        java.lang.String name,
        Holder<Integer> value);

    /**
     * SetPropertyString
     */
    @VTID(26)
    boolean setPropertyString(
        java.lang.String name,
        java.lang.String value);

    /**
     * GetPropertyString
     */
    @VTID(27)
    boolean getPropertyString(
        java.lang.String name,
        Holder<java.lang.String> value);

    /**
     * IsUISupported
     */
    @VTID(28)
    boolean isUISupported(
        java.lang.String typeOfUI,
        @DefaultValue("")java.lang.Object extraData);

    /**
     * DisplayUI
     */
    @VTID(29)
    void displayUI(
        int hWndParent,
        java.lang.String title,
        java.lang.String typeOfUI,
        @DefaultValue("")java.lang.Object extraData);

    /**
     * GetRecognizers
     */
    @VTID(30)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokens getRecognizers(
        @DefaultValue("")java.lang.String requiredAttributes,
        @DefaultValue("")java.lang.String optionalAttributes);

    /**
     * GetAudioInputs
     */
    @VTID(31)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokens getAudioInputs(
        @DefaultValue("")java.lang.String requiredAttributes,
        @DefaultValue("")java.lang.String optionalAttributes);

    /**
     * GetProfiles
     */
    @VTID(32)
    se_tpb_speechgen2.external.win.sapi5.ISpeechObjectTokens getProfiles(
        @DefaultValue("")java.lang.String requiredAttributes,
        @DefaultValue("")java.lang.String optionalAttributes);

}
