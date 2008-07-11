package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

public enum SpeechDiscardType implements ComEnum {
    SDTProperty(1),
    SDTReplacement(2),
    SDTRule(4),
    SDTDisplayText(8),
    SDTLexicalForm(16),
    SDTPronunciation(32),
    SDTAudio(64),
    SDTAlternates(128),
    SDTAll(255),
    ;

    private final int value;
    SpeechDiscardType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
