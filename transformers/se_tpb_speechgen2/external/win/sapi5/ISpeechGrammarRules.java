package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 * ISpeechGrammarRules Interface
 */
@IID("{6FFA3B44-FC2D-40D1-8AFC-32911C7F1AD1}")
public interface ISpeechGrammarRules extends Com4jObject,Iterable<Com4jObject> {
  // Methods:
  /**
   * <p>
   * Count
   * </p>
   * <p>
   * Getter method for the COM property "Count"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int count();


  /**
   * <p>
   * FindRule
   * </p>
   * @param ruleNameOrId Mandatory java.lang.Object parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(8)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule findRule(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ruleNameOrId);


  /**
   * <p>
   * Item
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(9)
  @DefaultMethod
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule item(
    int index);


  /**
   * <p>
   * Enumerates the alternates
   * </p>
   * <p>
   * Getter method for the COM property "_NewEnum"
   * </p>
   */

  @DISPID(-4) //= 0xfffffffc. The runtime will prefer the VTID if present
  @VTID(10)
  java.util.Iterator<Com4jObject> iterator();

  /**
   * <p>
   * Dynamic
   * </p>
   * <p>
   * Getter method for the COM property "Dynamic"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(11)
  boolean dynamic();


  /**
   * <p>
   * Add
   * </p>
   * @param ruleName Mandatory java.lang.String parameter.
   * @param attributes Mandatory se_tpb_speechgen2.external.win.sapi5.SpeechRuleAttributes parameter.
   * @param ruleId Optional parameter. Default value is 0
   * @return  Returns a value of type se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(12)
  se_tpb_speechgen2.external.win.sapi5.ISpeechGrammarRule add(
    java.lang.String ruleName,
    se_tpb_speechgen2.external.win.sapi5.SpeechRuleAttributes attributes,
    @Optional @DefaultValue("0") int ruleId);


  /**
   * <p>
   * Commit
   * </p>
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(13)
  void commit();


  /**
   * <p>
   * CommitAndSave
   * </p>
   * @param errorText Mandatory Holder<java.lang.String> parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(14)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object commitAndSave(
    Holder<java.lang.String> errorText);


  // Properties:
}
