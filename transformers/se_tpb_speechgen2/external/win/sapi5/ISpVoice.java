package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpVoice Interface
 */
@IID("{6C44DF74-72B9-4992-A1EC-EF996E0422D4}")
public interface ISpVoice extends se_tpb_speechgen2.external.win.sapi5.ISpEventSource {
    @VTID(13)
    void setOutput(
        com4j.Com4jObject pUnkOutput,
        int fAllowFormatChanges);

    @VTID(14)
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getOutputObjectToken();

    @VTID(15)
    se_tpb_speechgen2.external.win.sapi5.ISpStreamFormat getOutputStream();

    @VTID(16)
    void pause();

    @VTID(17)
    void resume();

    @VTID(18)
    void setVoice(
        se_tpb_speechgen2.external.win.sapi5.ISpObjectToken pToken);

    @VTID(19)
    se_tpb_speechgen2.external.win.sapi5.ISpObjectToken getVoice();

    @VTID(20)
    int speak(
        @MarshalAs(NativeType.Unicode) java.lang.String pwcs,
        int dwFlags);

    @VTID(21)
    int speakStream(
        se_tpb_speechgen2.external.win.sapi5.IStream pStream,
        int dwFlags);

        @VTID(23)
        int skip(
            @MarshalAs(NativeType.Unicode) java.lang.String pItemType,
            int lNumItems);

        @VTID(24)
        void setPriority(
            se_tpb_speechgen2.external.win.sapi5.SPVPRIORITY ePriority);

        @VTID(25)
        se_tpb_speechgen2.external.win.sapi5.SPVPRIORITY getPriority();

        @VTID(26)
        void setAlertBoundary(
            se_tpb_speechgen2.external.win.sapi5.SPEVENTENUM eBoundary);

        @VTID(27)
        se_tpb_speechgen2.external.win.sapi5.SPEVENTENUM getAlertBoundary();

        @VTID(28)
        void setRate(
            int rateAdjust);

        @VTID(29)
        int getRate();

        @VTID(30)
        void setVolume(
            short usVolume);

        @VTID(31)
        short getVolume();

        @VTID(32)
        void waitUntilDone(
            int msTimeout);

        @VTID(33)
        void setSyncSpeakTimeout(
            int msTimeout);

        @VTID(34)
        int getSyncSpeakTimeout();

        @VTID(35)
        void speakCompleteEvent();

        @VTID(36)
        int isUISupported(
            @MarshalAs(NativeType.Unicode) java.lang.String pszTypeOfUI,
            java.nio.Buffer pvExtraData,
            int cbExtraData);

        @VTID(37)
        void displayUI(
            int hWndParent,
            @MarshalAs(NativeType.Unicode) java.lang.String pszTitle,
            @MarshalAs(NativeType.Unicode) java.lang.String pszTypeOfUI,
            java.nio.Buffer pvExtraData,
            int cbExtraData);

    }
