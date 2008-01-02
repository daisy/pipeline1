package se_tpb_speechgen2.external.MacOS;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

import se_tpb_speechgen2.audio.AudioFiles;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.adapters.AbstractTTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * 
 * TTS Adapter that use the Mac OS X built '/usr/bin/say' command for speech
 * synthesis .
 * 
 * @author Romain Deltour
 * 
 */
public class MacSayTTS extends AbstractTTSAdapter {

	public MacSayTTS(TTSUtils ttsUtils, Map<?, ?> params) {
		super(ttsUtils, params);
	}

	@Override
	public long read(String line, File destination) throws TTSException {
		String destName = destination.getAbsolutePath();
		String aiffName = destName + ".aiff";
		String say = "/usr/bin/say";
		String sox = System.getProperty("pipeline.sox.path");
		String[] cmd = null;
		try {
			cmd = new String[] { say, "-o", aiffName, line };
			Runtime.getRuntime().exec(cmd).waitFor();
			cmd = new String[] { sox, aiffName, "-t", "wav", destName };
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (IOException e) {
			throw new TTSException("Could not execute: " + cmd, e);
		} catch (InterruptedException e) {
			throw new TTSException("Command was interrupted: " + cmd, e);
		} finally{
			(new File(aiffName)).delete();
		}
		try {
			return AudioFiles.getAudioFileDuration(destination);
		} catch (UnsupportedAudioFileException e) {
			throw new TTSException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TTSException(e.getMessage(), e);
		}
	}
}
