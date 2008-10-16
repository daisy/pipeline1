package se_tpb_speechgen2.external.linux;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.adapters.AbstractTTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * An adapter for eSpeak 
 * @author Markus Gylling
 */
public class ESpeakTTS extends AbstractTTSAdapter {

	public ESpeakTTS(TTSUtils ttsUtils, Map<String, String> params) {
		super(ttsUtils, params);
	}

	@Override
	public void read(String line, File destination) throws IOException, TTSException {
				
		String eSpeak = "espeak";
		String voiceFile = mParams.get("eSpeakVoiceFile");
		if(voiceFile==null||voiceFile.length()<1) voiceFile = "default";
		String dest = destination.getAbsolutePath();
		
		String[] cmd = null;
		try{
			cmd = new String[] {eSpeak, "-w", dest, "-v", voiceFile, "\""+line+"\""};
			Runtime.getRuntime().exec(cmd).waitFor();		
		} catch (Exception e) {
			throw new TTSException(e.getMessage(), e);
		}		
	}

}
