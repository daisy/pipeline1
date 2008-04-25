package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechObjectTokenCategory Interface
 */
@IID("{CA7EAC50-2D01-4145-86D4-5AE7D70F4469}")
public interface ISpeechObjectTokenCategory extends Com4jObject {
    /**
     * Id
     */
    @VTID(7)
    java.lang.String id();

    /**
     * Default
     */
    @VTID(8)
    void _default(
        java.lang.String tokenId);

    /**
     * Default
     */
    @VTID(9)
    java.lang.String _default();

    /**
     * SetId
     */
    @VTID(10)
    void setId(
        java.lang.String id,
        @DefaultValue("0")boolean createIfNotExist);

    /**
     * GetDataKey
     */
    @VTID(11)
    org.daisy.util.wincom.sapi5.ISpeechDataKey getDataKey(
        @DefaultValue("0")org.daisy.util.wincom.sapi5.SpeechDataKeyLocation location);

    /**
     * EnumerateTokens
     */
    @VTID(12)
    org.daisy.util.wincom.sapi5.ISpeechObjectTokens enumerateTokens(
        @DefaultValue("")java.lang.String requiredAttributes,
        @DefaultValue("")java.lang.String optionalAttributes);

}
