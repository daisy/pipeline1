package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 */
public enum SPLEXICONTYPE implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  eLEXTYPE_USER(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  eLEXTYPE_APP(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  eLEXTYPE_VENDORLEXICON(4),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  eLEXTYPE_LETTERTOSOUND(8),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  eLEXTYPE_MORPHOLOGY(16),
  /**
   * <p>
   * The value of this constant is 32
   * </p>
   */
  eLEXTYPE_GRAMMAR(32),
  /**
   * <p>
   * The value of this constant is 64
   * </p>
   */
  eLEXTYPE_USER_SHORTCUT(64),
  /**
   * <p>
   * The value of this constant is 128
   * </p>
   */
  eLEXTYPE_RESERVED6(128),
  /**
   * <p>
   * The value of this constant is 256
   * </p>
   */
  eLEXTYPE_RESERVED7(256),
  /**
   * <p>
   * The value of this constant is 512
   * </p>
   */
  eLEXTYPE_RESERVED8(512),
  /**
   * <p>
   * The value of this constant is 1024
   * </p>
   */
  eLEXTYPE_RESERVED9(1024),
  /**
   * <p>
   * The value of this constant is 2048
   * </p>
   */
  eLEXTYPE_RESERVED10(2048),
  /**
   * <p>
   * The value of this constant is 4096
   * </p>
   */
  eLEXTYPE_PRIVATE1(4096),
  /**
   * <p>
   * The value of this constant is 8192
   * </p>
   */
  eLEXTYPE_PRIVATE2(8192),
  /**
   * <p>
   * The value of this constant is 16384
   * </p>
   */
  eLEXTYPE_PRIVATE3(16384),
  /**
   * <p>
   * The value of this constant is 32768
   * </p>
   */
  eLEXTYPE_PRIVATE4(32768),
  /**
   * <p>
   * The value of this constant is 65536
   * </p>
   */
  eLEXTYPE_PRIVATE5(65536),
  /**
   * <p>
   * The value of this constant is 131072
   * </p>
   */
  eLEXTYPE_PRIVATE6(131072),
  /**
   * <p>
   * The value of this constant is 262144
   * </p>
   */
  eLEXTYPE_PRIVATE7(262144),
  /**
   * <p>
   * The value of this constant is 524288
   * </p>
   */
  eLEXTYPE_PRIVATE8(524288),
  /**
   * <p>
   * The value of this constant is 1048576
   * </p>
   */
  eLEXTYPE_PRIVATE9(1048576),
  /**
   * <p>
   * The value of this constant is 2097152
   * </p>
   */
  eLEXTYPE_PRIVATE10(2097152),
  /**
   * <p>
   * The value of this constant is 4194304
   * </p>
   */
  eLEXTYPE_PRIVATE11(4194304),
  /**
   * <p>
   * The value of this constant is 8388608
   * </p>
   */
  eLEXTYPE_PRIVATE12(8388608),
  /**
   * <p>
   * The value of this constant is 16777216
   * </p>
   */
  eLEXTYPE_PRIVATE13(16777216),
  /**
   * <p>
   * The value of this constant is 33554432
   * </p>
   */
  eLEXTYPE_PRIVATE14(33554432),
  /**
   * <p>
   * The value of this constant is 67108864
   * </p>
   */
  eLEXTYPE_PRIVATE15(67108864),
  /**
   * <p>
   * The value of this constant is 134217728
   * </p>
   */
  eLEXTYPE_PRIVATE16(134217728),
  /**
   * <p>
   * The value of this constant is 268435456
   * </p>
   */
  eLEXTYPE_PRIVATE17(268435456),
  /**
   * <p>
   * The value of this constant is 536870912
   * </p>
   */
  eLEXTYPE_PRIVATE18(536870912),
  /**
   * <p>
   * The value of this constant is 1073741824
   * </p>
   */
  eLEXTYPE_PRIVATE19(1073741824),
  /**
   * <p>
   * The value of this constant is -2147483648
   * </p>
   */
  eLEXTYPE_PRIVATE20(-2147483648),
  ;

  private final int value;
  SPLEXICONTYPE(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
