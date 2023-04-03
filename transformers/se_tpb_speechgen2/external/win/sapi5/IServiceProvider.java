package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

@IID("{6D5140C1-7436-11CE-8034-00AA006009FA}")
public interface IServiceProvider extends Com4jObject {
  // Methods:
  /**
   * @param guidService Mandatory GUID parameter.
   * @param riid Mandatory GUID parameter.
   * @return  Returns a value of type com4j.Com4jObject
   */

  @VTID(3)
  com4j.Com4jObject remoteQueryService(
    GUID guidService,
    GUID riid);


  // Properties:
}
