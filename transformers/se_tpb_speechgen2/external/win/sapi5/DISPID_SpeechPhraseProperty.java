package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum DISPID_SpeechPhraseProperty implements ComEnum {
    DISPID_SPPName(1),
    DISPID_SPPId(2),
    DISPID_SPPValue(3),
    DISPID_SPPFirstElement(4),
    DISPID_SPPNumberOfElements(5),
    DISPID_SPPEngineConfidence(6),
    DISPID_SPPConfidence(7),
    DISPID_SPPParent(8),
    DISPID_SPPChildren(9),
    ;

    private final int value;
    DISPID_SpeechPhraseProperty(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
