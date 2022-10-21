package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 */
public enum SPPREDICTORNAME implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SAPI_CONF_PRED_TYPE_CONFUSABILITY(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ACOUSTIC_NORMALIZED(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  SAPI_CONF_PRED_TYPE_BACKGROUND_NORMALIZED(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SAPI_CONF_PRED_TYPE_NOISE_NORMALIZED(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ARC_ACOUSTIC_NORMALIZED(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LM_NORMALIZED(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  SAPI_CONF_PRED_TYPE_DURATION_NORMALIZED(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SAPI_CONF_PRED_TYPE_BIN_CONFUSABILITY(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LM_PERPLEXITY(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LM_FANOUT(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ACTIVE_SENONES(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ACTIVE_CHANNELS(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LM_DUR_NORMALIZED(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  SAPI_CONF_PRED_TYPE_DURATION(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  SAPI_CONF_PRED_TYPE_2ND_ORDER_PRODUCT(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  SAPI_CONF_PRED_TYPE_2ND_ORDER_AVERAGE(16),
  /**
   * <p>
   * The value of this constant is 17
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ACOUSTIC_NORMALIZED_0(17),
  /**
   * <p>
   * The value of this constant is 18
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ACOUSTIC_NORMALIZED_1(18),
  /**
   * <p>
   * The value of this constant is 19
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ACOUSTIC_NORMALIZED_2(19),
  /**
   * <p>
   * The value of this constant is 20
   * </p>
   */
  SAPI_CONF_PRED_TYPE_SIL_ACOUSTIC_NORMALIZED(20),
  /**
   * <p>
   * The value of this constant is 21
   * </p>
   */
  SAPI_CONF_PRED_TYPE_SIL_ACOUSTIC_NORMALIZED_0(21),
  /**
   * <p>
   * The value of this constant is 22
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LOG_POSTERIOR_0(22),
  /**
   * <p>
   * The value of this constant is 23
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LOG_POSTERIOR_1(23),
  /**
   * <p>
   * The value of this constant is 24
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LOG_POSTERIOR_2(24),
  /**
   * <p>
   * The value of this constant is 25
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LOG_POSTERIOR_3(25),
  /**
   * <p>
   * The value of this constant is 26
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LOG_POSTERIOR_4(26),
  /**
   * <p>
   * The value of this constant is 27
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LOG_POSTERIOR_5(27),
  /**
   * <p>
   * The value of this constant is 28
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LOG_POSTERIOR_6(28),
  /**
   * <p>
   * The value of this constant is 29
   * </p>
   */
  SAPI_CONF_PRED_TYPE_LOG_POSTERIOR_7(29),
  /**
   * <p>
   * The value of this constant is 30
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ACOUSTIC_NORMALIZED_WORD_MIN(30),
  /**
   * <p>
   * The value of this constant is 31
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ACOUSTIC_NORMALIZED_WORD_CONFWAVG(31),
  /**
   * <p>
   * The value of this constant is 32
   * </p>
   */
  SAPI_CONF_PRED_TYPE_WORD_COUNT(32),
  /**
   * <p>
   * The value of this constant is 33
   * </p>
   */
  SAPI_CONF_PRED_TYPE_2ND_ORDER_PRODUCTNORM(33),
  /**
   * <p>
   * The value of this constant is 34
   * </p>
   */
  SAPI_CONF_PRED_TYPE_STARTFRAME(34),
  /**
   * <p>
   * The value of this constant is 35
   * </p>
   */
  SAPI_CONF_PRED_TYPE_ENDFRAME(35),
  ;

  private final int value;
  SPPREDICTORNAME(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
