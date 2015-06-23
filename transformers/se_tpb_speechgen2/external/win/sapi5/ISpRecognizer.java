package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpRecognizer Interface
 */
@IID("{C2B5F241-DAA0-4507-9E16-5A1EAA2B7A5C}")
public interface ISpRecognizer extends se_tpb_speechgen2.external.win.sapi5.ISpProperties {
    @VTID(7)
    void setRecognizer(
        se_tpb_speechgen2.external.win.sapi5.ISpObjectToken pRecognizer);

    @VTID(8)
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getRecognizer();

    @VTID(9)
    void setInput(
        com4j.Com4jObject pUnkInput,
        int fAllowFormatChanges);

    @VTID(10)
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getInputObjectToken();

    @VTID(11)
    se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat getInputStream();

    @VTID(12)
    se_tpb_speechgen2.external.win.sapi5.ISpRecoContext createRecoContext();

    @VTID(13)
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getRecoProfile();

    @VTID(14)
    void setRecoProfile(
        se_tpb_speechgen2.external.win.sapi5.ISpObjectToken pToken);

    @VTID(15)
    void isSharedInstance();

    @VTID(16)
    se_tpb_speechgen2.external.win.sapi5.SPRECOSTATE getRecoState();

    @VTID(17)
    void setRecoState(
        se_tpb_speechgen2.external.win.sapi5.SPRECOSTATE newState);

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
            se_tpb_speechgen2.external.win.sapi5.ISpPhrase pPhrase);

    }
