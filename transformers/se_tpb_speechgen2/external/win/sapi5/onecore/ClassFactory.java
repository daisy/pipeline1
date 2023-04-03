package se_tpb_speechgen2.external.win.sapi5.onecore  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  /**
   * SpNotify
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpNotifyTranslator createSpNotifyTranslator() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpNotifyTranslator.class, "{47B73B70-1964-444A-B390-FAB1565DCAA3}" );
  }

  /**
   * SpObjectTokenCategory Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpObjectTokenCategory createSpObjectTokenCategory() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpObjectTokenCategory.class, "{461DED9E-81D5-494F-BC96-6432C8645733}" );
  }

  /**
   * SpObjectToken Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpObjectToken createSpObjectToken() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpObjectToken.class, "{9FF65B15-F7B4-4858-BFE6-DB2083DEDF68}" );
  }

  /**
   * SpResourceManger
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpResourceManager createSpResourceManager() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpResourceManager.class, "{0EEF5FBE-18F3-478A-BD28-4899C2E90323}" );
  }

  /**
   * FormatConverter Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpStreamFormatConverter createSpStreamFormatConverter() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpStreamFormatConverter.class, "{25F843F3-1ED6-4B4E-8749-DF294C7FEB30}" );
  }

  /**
   * SpMMAudioEnum Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.IEnumSpObjectTokens createSpMMAudioEnum() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.IEnumSpObjectTokens.class, "{14E74C62-DC97-43B0-8F2F-581496A65D60}" );
  }

  /**
   * SpMMAudioIn Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpEventSource createSpMMAudioIn() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpEventSource.class, "{A63E13D5-5263-44A6-8A18-381EB6F181A2}" );
  }

  /**
   * SpMMAudioOut Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpEventSource createSpMMAudioOut() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpEventSource.class, "{BC065925-E8EF-4EF7-B8C5-006C8DB832D5}" );
  }

  /**
   * SpStream Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpStream createSpStream() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpStream.class, "{0DD1B55E-2EB8-42E2-B8D0-F11594A29477}" );
  }

  /**
   * SpVoice Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpVoice createSpVoice() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpVoice.class, "{9BC773B8-9B6C-400F-8AF0-0DFDD1C43229}" );
  }

  /**
   * SpSharedRecoContext Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoContext createSpSharedRecoContext() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecoContext.class, "{2797B627-B7FA-4CBF-9472-6B8ACFDF3E41}" );
  }

  /**
   * SpInprocRecognizer Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecognizer createSpInprocRecognizer() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecognizer.class, "{8A44FFB7-8140-432F-BEC5-1B5FF725BAC8}" );
  }

  /**
   * SpSharedRecognizer Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecognizer createSpSharedRecognizer() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpRecognizer.class, "{846225EA-B241-42A7-9D46-A694ECD5A781}" );
  }

  /**
   * SpLexicon Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpLexicon createSpLexicon() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpLexicon.class, "{67677441-3350-45B4-9455-4D7F4A50F4E4}" );
  }

  /**
   * SpUnCompressedLexicon Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpLexicon createSpUnCompressedLexicon() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpLexicon.class, "{D1915118-9D27-4C69-B82E-7955DAF57201}" );
  }

  /**
   * SpCompressedLexicon Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpLexicon createSpCompressedLexicon() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpLexicon.class, "{D65B9994-6416-41ED-84EE-08733558C381}" );
  }

  /**
   * SpShortcut Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpShortcut createSpShortcut() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpShortcut.class, "{B786ECB7-8942-4FE1-9DCD-230E30DE5AFF}" );
  }

  /**
   * SpPLSLexicon Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpLexicon2 createSpPLSLexicon() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpLexicon2.class, "{D5029E5D-F109-4BC0-B6DB-07F92D6ABAE7}" );
  }

  /**
   * SpPhoneConverter Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhoneConverter createSpPhoneConverter() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhoneConverter.class, "{C6FABB24-E332-46FB-BC91-FF331B2D51F0}" );
  }

  /**
   * SpPhoneticAlphabetConverter Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhoneticAlphabetConverter createSpPhoneticAlphabetConverter() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhoneticAlphabetConverter.class, "{17383A17-6B93-4193-9B60-4D4F99C05163}" );
  }

  /**
   * SpNullPhoneConverter Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhoneConverter createSpNullPhoneConverter() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpPhoneConverter.class, "{4F4DD15E-F431-4536-AEE8-AF20BA847A33}" );
  }

  /**
   * SpFileStream Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpStream createSpFileStream() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpStream.class, "{62547D24-2ACA-4A33-8F15-AE95CE1A45BD}" );
  }

  /**
   * CSpPredictorSet Class
   */
  public static se_tpb_speechgen2.external.win.sapi5.onecore.ISpPredictorSet createCSpPredictorSet() {
    return COM4J.createInstance( se_tpb_speechgen2.external.win.sapi5.onecore.ISpPredictorSet.class, "{53E9771B-6A21-4FEC-B8F1-3B0DAE4FACC6}" );
  }
}
