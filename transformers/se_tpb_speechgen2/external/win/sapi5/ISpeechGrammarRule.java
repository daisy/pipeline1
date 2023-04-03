package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechGrammarRule Interface
 */
@IID("{AFE719CF-5DD1-44F2-999C-7A399F1CFCCC}")
public interface ISpeechGrammarRule extends Com4jObject {
  // Methods:
  /**
   * <p>
   * RuleAttributes
   * </p>
   * <p>
   * Getter method for the COM property "Attributes"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.SpeechRuleAttributes
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  se_tpb_speechgen2.external.win.sapi5.SpeechRuleAttributes attributes();


  /**
   * <p>
   * InitialState
   * </p>
   * <p>
   * Getter method for the COM property "InitialState"
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState initialState();


  /**
   * <p>
   * Name
   * </p>
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String name();


  /**
   * <p>
   * Id
   * </p>
   * <p>
   * Getter method for the COM property "Id"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  int id();


  /**
   * <p>
   * Clear
   * </p>
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  void clear();


  /**
   * <p>
   * AddResource
   * </p>
   * @param resourceName Mandatory java.lang.String parameter.
   * @param resourceValue Mandatory java.lang.String parameter.
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  void addResource(
    java.lang.String resourceName,
    java.lang.String resourceValue);


  /**
   * <p>
   * AddState
   * </p>
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRuleState addState();


  // Properties:
}
