package se_tpb_speechgen2.external.win.sapi5  ;

import com4j.*;

/**
 */
public enum SpeechAudioFormatType implements ComEnum {
  /**
   * <p>
   * The value of this constant is -1
   * </p>
   */
  SAFTDefault(-1),
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  SAFTNoAssignedFormat(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  SAFTText(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  SAFTNonStandardFormat(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  SAFTExtendedAudioFormat(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  SAFT8kHz8BitMono(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  SAFT8kHz8BitStereo(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  SAFT8kHz16BitMono(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  SAFT8kHz16BitStereo(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  SAFT11kHz8BitMono(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  SAFT11kHz8BitStereo(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  SAFT11kHz16BitMono(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  SAFT11kHz16BitStereo(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  SAFT12kHz8BitMono(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  SAFT12kHz8BitStereo(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  SAFT12kHz16BitMono(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  SAFT12kHz16BitStereo(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  SAFT16kHz8BitMono(16),
  /**
   * <p>
   * The value of this constant is 17
   * </p>
   */
  SAFT16kHz8BitStereo(17),
  /**
   * <p>
   * The value of this constant is 18
   * </p>
   */
  SAFT16kHz16BitMono(18),
  /**
   * <p>
   * The value of this constant is 19
   * </p>
   */
  SAFT16kHz16BitStereo(19),
  /**
   * <p>
   * The value of this constant is 20
   * </p>
   */
  SAFT22kHz8BitMono(20),
  /**
   * <p>
   * The value of this constant is 21
   * </p>
   */
  SAFT22kHz8BitStereo(21),
  /**
   * <p>
   * The value of this constant is 22
   * </p>
   */
  SAFT22kHz16BitMono(22),
  /**
   * <p>
   * The value of this constant is 23
   * </p>
   */
  SAFT22kHz16BitStereo(23),
  /**
   * <p>
   * The value of this constant is 24
   * </p>
   */
  SAFT24kHz8BitMono(24),
  /**
   * <p>
   * The value of this constant is 25
   * </p>
   */
  SAFT24kHz8BitStereo(25),
  /**
   * <p>
   * The value of this constant is 26
   * </p>
   */
  SAFT24kHz16BitMono(26),
  /**
   * <p>
   * The value of this constant is 27
   * </p>
   */
  SAFT24kHz16BitStereo(27),
  /**
   * <p>
   * The value of this constant is 28
   * </p>
   */
  SAFT32kHz8BitMono(28),
  /**
   * <p>
   * The value of this constant is 29
   * </p>
   */
  SAFT32kHz8BitStereo(29),
  /**
   * <p>
   * The value of this constant is 30
   * </p>
   */
  SAFT32kHz16BitMono(30),
  /**
   * <p>
   * The value of this constant is 31
   * </p>
   */
  SAFT32kHz16BitStereo(31),
  /**
   * <p>
   * The value of this constant is 32
   * </p>
   */
  SAFT44kHz8BitMono(32),
  /**
   * <p>
   * The value of this constant is 33
   * </p>
   */
  SAFT44kHz8BitStereo(33),
  /**
   * <p>
   * The value of this constant is 34
   * </p>
   */
  SAFT44kHz16BitMono(34),
  /**
   * <p>
   * The value of this constant is 35
   * </p>
   */
  SAFT44kHz16BitStereo(35),
  /**
   * <p>
   * The value of this constant is 36
   * </p>
   */
  SAFT48kHz8BitMono(36),
  /**
   * <p>
   * The value of this constant is 37
   * </p>
   */
  SAFT48kHz8BitStereo(37),
  /**
   * <p>
   * The value of this constant is 38
   * </p>
   */
  SAFT48kHz16BitMono(38),
  /**
   * <p>
   * The value of this constant is 39
   * </p>
   */
  SAFT48kHz16BitStereo(39),
  /**
   * <p>
   * The value of this constant is 40
   * </p>
   */
  SAFTTrueSpeech_8kHz1BitMono(40),
  /**
   * <p>
   * The value of this constant is 41
   * </p>
   */
  SAFTCCITT_ALaw_8kHzMono(41),
  /**
   * <p>
   * The value of this constant is 42
   * </p>
   */
  SAFTCCITT_ALaw_8kHzStereo(42),
  /**
   * <p>
   * The value of this constant is 43
   * </p>
   */
  SAFTCCITT_ALaw_11kHzMono(43),
  /**
   * <p>
   * The value of this constant is 44
   * </p>
   */
  SAFTCCITT_ALaw_11kHzStereo(44),
  /**
   * <p>
   * The value of this constant is 45
   * </p>
   */
  SAFTCCITT_ALaw_22kHzMono(45),
  /**
   * <p>
   * The value of this constant is 46
   * </p>
   */
  SAFTCCITT_ALaw_22kHzStereo(46),
  /**
   * <p>
   * The value of this constant is 47
   * </p>
   */
  SAFTCCITT_ALaw_44kHzMono(47),
  /**
   * <p>
   * The value of this constant is 48
   * </p>
   */
  SAFTCCITT_ALaw_44kHzStereo(48),
  /**
   * <p>
   * The value of this constant is 49
   * </p>
   */
  SAFTCCITT_uLaw_8kHzMono(49),
  /**
   * <p>
   * The value of this constant is 50
   * </p>
   */
  SAFTCCITT_uLaw_8kHzStereo(50),
  /**
   * <p>
   * The value of this constant is 51
   * </p>
   */
  SAFTCCITT_uLaw_11kHzMono(51),
  /**
   * <p>
   * The value of this constant is 52
   * </p>
   */
  SAFTCCITT_uLaw_11kHzStereo(52),
  /**
   * <p>
   * The value of this constant is 53
   * </p>
   */
  SAFTCCITT_uLaw_22kHzMono(53),
  /**
   * <p>
   * The value of this constant is 54
   * </p>
   */
  SAFTCCITT_uLaw_22kHzStereo(54),
  /**
   * <p>
   * The value of this constant is 55
   * </p>
   */
  SAFTCCITT_uLaw_44kHzMono(55),
  /**
   * <p>
   * The value of this constant is 56
   * </p>
   */
  SAFTCCITT_uLaw_44kHzStereo(56),
  /**
   * <p>
   * The value of this constant is 57
   * </p>
   */
  SAFTADPCM_8kHzMono(57),
  /**
   * <p>
   * The value of this constant is 58
   * </p>
   */
  SAFTADPCM_8kHzStereo(58),
  /**
   * <p>
   * The value of this constant is 59
   * </p>
   */
  SAFTADPCM_11kHzMono(59),
  /**
   * <p>
   * The value of this constant is 60
   * </p>
   */
  SAFTADPCM_11kHzStereo(60),
  /**
   * <p>
   * The value of this constant is 61
   * </p>
   */
  SAFTADPCM_22kHzMono(61),
  /**
   * <p>
   * The value of this constant is 62
   * </p>
   */
  SAFTADPCM_22kHzStereo(62),
  /**
   * <p>
   * The value of this constant is 63
   * </p>
   */
  SAFTADPCM_44kHzMono(63),
  /**
   * <p>
   * The value of this constant is 64
   * </p>
   */
  SAFTADPCM_44kHzStereo(64),
  /**
   * <p>
   * The value of this constant is 65
   * </p>
   */
  SAFTGSM610_8kHzMono(65),
  /**
   * <p>
   * The value of this constant is 66
   * </p>
   */
  SAFTGSM610_11kHzMono(66),
  /**
   * <p>
   * The value of this constant is 67
   * </p>
   */
  SAFTGSM610_22kHzMono(67),
  /**
   * <p>
   * The value of this constant is 68
   * </p>
   */
  SAFTGSM610_44kHzMono(68),
  ;

  private final int value;
  SpeechAudioFormatType(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
