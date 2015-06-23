package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechVoiceStatus Interface
 */
@IID("{8BE47B07-57F6-11D2-9EEE-00C04F797396}")
public interface ISpeechVoiceStatus extends Com4jObject {
    /**
     * CurrentStreamNumber
     */
    @VTID(7)
    int currentStreamNumber();

    /**
     * LastStreamNumberQueued
     */
    @VTID(8)
    int lastStreamNumberQueued();

    /**
     * LastHResult
     */
    @VTID(9)
    int lastHResult();

    /**
     * RunningState
     */
    @VTID(10)
    se_tpb_speechgen2.external.win.sapi5.SpeechRunState runningState();

    /**
     * InputWordPosition
     */
    @VTID(11)
    int inputWordPosition();

    /**
     * InputWordLength
     */
    @VTID(12)
    int inputWordLength();

    /**
     * InputSentencePosition
     */
    @VTID(13)
    int inputSentencePosition();

    /**
     * InputSentenceLength
     */
    @VTID(14)
    int inputSentenceLength();

    /**
     * LastBookmark
     */
    @VTID(15)
    java.lang.String lastBookmark();

    /**
     * LastBookmarkId
     */
    @VTID(16)
    int lastBookmarkId();

    /**
     * PhonemeId
     */
    @VTID(17)
    short phonemeId();

    /**
     * VisemeId
     */
    @VTID(18)
    short visemeId();

}
