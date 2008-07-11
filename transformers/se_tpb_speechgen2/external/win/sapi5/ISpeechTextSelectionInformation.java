package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechTextSelectionInformation Interface
 */
@IID("{3B9C7E7A-6EEE-4DED-9092-11657279ADBE}")
public interface ISpeechTextSelectionInformation extends Com4jObject {
    /**
     * ActiveOffset
     */
    @VTID(7)
    void activeOffset(
        int activeOffset);

    /**
     * ActiveOffset
     */
    @VTID(8)
    int activeOffset();

    /**
     * ActiveLength
     */
    @VTID(9)
    void activeLength(
        int activeLength);

    /**
     * ActiveLength
     */
    @VTID(10)
    int activeLength();

    /**
     * SelectionOffset
     */
    @VTID(11)
    void selectionOffset(
        int selectionOffset);

    /**
     * SelectionOffset
     */
    @VTID(12)
    int selectionOffset();

    /**
     * SelectionLength
     */
    @VTID(13)
    void selectionLength(
        int selectionLength);

    /**
     * SelectionLength
     */
    @VTID(14)
    int selectionLength();

}
