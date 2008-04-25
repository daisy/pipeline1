package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpRecognizer Interface
 */
@IID("{C2B5F241-DAA0-4507-9E16-5A1EAA2B7A5C}")
public interface ISpRecognizer extends org.daisy.util.wincom.sapi5.ISpProperties {
    @VTID(7)
    void setRecognizer(
        org.daisy.util.wincom.sapi5.ISpObjectToken pRecognizer);

    @VTID(8)
    org.daisy.util.wincom.sapi5.ISpObjectToken getRecognizer();

    @VTID(9)
    void setInput(
        com4j.Com4jObject pUnkInput,
        int fAllowFormatChanges);

    @VTID(10)
    org.daisy.util.wincom.sapi5.ISpObjectToken getInputObjectToken();

    @VTID(11)
    org.daisy.util.wincom.sapi5.ISpStreamFormat getInputStream();

    @VTID(12)
    org.daisy.util.wincom.sapi5.ISpRecoContext createRecoContext();

    @VTID(13)
    org.daisy.util.wincom.sapi5.ISpObjectToken getRecoProfile();

    @VTID(14)
    void setRecoProfile(
        org.daisy.util.wincom.sapi5.ISpObjectToken pToken);

    @VTID(15)
    void isSharedInstance();

    @VTID(16)
    org.daisy.util.wincom.sapi5.SPRECOSTATE getRecoState();

    @VTID(17)
    void setRecoState(
        org.daisy.util.wincom.sapi5.SPRECOSTATE newState);

        @VTID(20)
        int isUISupported(
            @MarshalAs(NativeType.Unicode) java.lang.String pszTypeOfUI,
            java.nio.Buffer pvExtraData,
            int cbExtraData);

        @VTID(21)
        void displayUI(
            int hWndParent,
            @MarshalAs(NativeType.Unicode) java.lang.String pszTitle,
            @MarshalAs(NativeType.Unicode) java.lang.String pszTypeOfUI,
            java.nio.Buffer pvExtraData,
            int cbExtraData);

        @VTID(22)
        void emulateRecognition(
            org.daisy.util.wincom.sapi5.ISpPhrase pPhrase);

    }
