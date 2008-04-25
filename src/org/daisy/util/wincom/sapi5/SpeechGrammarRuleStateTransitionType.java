package org.daisy.util.wincom.sapi5  ;

import com4j.*;

public enum SpeechGrammarRuleStateTransitionType {
    SGRSTTEpsilon, // 0
    SGRSTTWord, // 1
    SGRSTTRule, // 2
    SGRSTTDictation, // 3
    SGRSTTWildcard, // 4
    SGRSTTTextBuffer, // 5
}
