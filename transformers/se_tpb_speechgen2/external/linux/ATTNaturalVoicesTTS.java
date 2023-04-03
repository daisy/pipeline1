package se_tpb_speechgen2.external.linux;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Map;

import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.adapters.AbstractTTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * An adapter for AT&amp;T Natural Voices
 * 
 * This is a fairly hackish implementation of an adaptor for the AT&amp;T Natural Voices TTS.
 * It is hackish in the sense that it hard codes where the TTS is installed (see ATT_ROOTDIR)
 * and also in that it uses the command line interface of the TTS when there would be a fairly
 * clean Java API. However this would probably require the distribution of the AT&amp;T TTS jars
 * in order to be able to build the pipeline. This is probably not what we want.
 * 
 * @author Christian Egli
 */
public class ATTNaturalVoicesTTS extends AbstractTTSAdapter {

	static final String ATT_ROOTDIR = "/usr/local/ATTNaturalVoices/TTS1.4.1/Desktop";

	public ATTNaturalVoicesTTS(TTSUtils ttsUtils, Map<String, String> params) {
		super(ttsUtils, params);
	}

	@Override
	public void read(String line, File destination) throws TTSException {

		String command = ATT_ROOTDIR + "/bin/TTSStandaloneFileDT";
		String dataDir = ATT_ROOTDIR + "/data";
		String dest = destination.getAbsolutePath();

		String[] cmd = new String[] { command, "-o", dest, "-data", dataDir };

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedWriter os = new BufferedWriter(new OutputStreamWriter(p
					.getOutputStream()));
			os.write(line);
			os.flush();
			os.close();
			p.waitFor();
		} catch (Exception e) {
			throw new TTSException(e.getMessage(), e);
		}
	}
}
