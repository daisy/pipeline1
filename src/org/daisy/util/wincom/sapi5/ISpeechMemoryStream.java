package org.daisy.util.wincom.sapi5  ;

import com4j.*;

/**
 * ISpeechMemoryStream Interface
 */
@IID("{EEB14B68-808B-4ABE-A5EA-B51DA7588008}")
public interface ISpeechMemoryStream extends org.daisy.util.wincom.sapi5.ISpeechBaseStream {
    /**
     * SetData
     */
    @VTID(12)
    void setData(
        @MarshalAs(NativeType.VARIANT) java.lang.Object data);

    /**
     * GetData
     */
    @VTID(13)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object getData();

}
