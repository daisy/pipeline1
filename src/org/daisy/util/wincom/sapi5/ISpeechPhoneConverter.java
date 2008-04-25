package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechPhoneConverter Interface
 */
@IID("{C3E4F353-433F-43D6-89A1-6A62A7054C3D}")
public interface ISpeechPhoneConverter extends Com4jObject {
    /**
     * LanguageId
     */
    @VTID(7)
    int languageId();

    /**
     * LanguageId
     */
    @VTID(8)
    void languageId(
        int languageId);

    /**
     * PhoneToId
     */
    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object phoneToId(
        java.lang.String phonemes);

    /**
     * IdToPhone
     */
    @VTID(10)
    java.lang.String idToPhone(
        @MarshalAs(NativeType.VARIANT) java.lang.Object idArray);

}
