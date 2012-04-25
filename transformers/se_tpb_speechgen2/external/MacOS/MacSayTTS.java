/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_speechgen2.external.MacOS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;

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

	private String voiceName;
	private String say = "/usr/bin/say";
	private String sox = System.getProperty("pipeline.sox.path");

	public MacSayTTS(TTSUtils ttsUtils, Map<String, String> params) {
		super(ttsUtils, params);
	}
	
	

	@Override
	public void init() {
		super.init();
		// Parameter "voice" is a coma-separated list of voices
		// We try to use the voices until we get one that works
		String voices = mParams.get("voice");
		if (voices != null) {
			Iterator<String> it = Arrays.asList(voices.split(",")).iterator();
			while (voiceName == null && it.hasNext()) {
				String voice = it.next().trim();
				try {
					File testFile = File.createTempFile("tmp", ".aiff");
					testFile.delete();
					Runtime.getRuntime().exec(
							new String[] { say, "-v", voice, "-o",
									testFile.getAbsolutePath(), "test" })
							.waitFor();
					if (testFile.exists()) {
						voiceName = voice;
						testFile.delete();
					} else {
						mTransformer.delegateMessage(this, "Cannot find voice \""
								+ voice + "\"", Type.INFO, Cause.SYSTEM, null);
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}
		if (voiceName == null) {
			mTransformer.delegateMessage(this, "No voice specified, using default voice", Type.INFO_FINER, Cause.SYSTEM, null);
		}
	}



	@Override
	public void read(String line, File destination) throws IOException,
			TTSException {
		String destName = destination.getAbsolutePath();
		String aiffName = destName + ".aiff";
		String txtName = destName + ".txt";
		String[] cmd = null;
		FileOutputStream fout=null;
		OutputStreamWriter writer=null;
		try {
			// String to Text
			fout = new FileOutputStream(txtName);
			writer = new OutputStreamWriter(fout,
					Charset.forName("UTF-8"));
			writer.write(line);
			writer.flush();
			
			// Text to AIFF
			if (voiceName != null) {
				cmd = new String[] { say, "-v", voiceName, "-o", aiffName, "-f", txtName };
			} else {
				cmd = new String[] { say, "-o", aiffName, "-f", txtName };
			}
			Runtime.getRuntime().exec(cmd).waitFor();
			
			// AIFF to WAV
			cmd = new String[] { sox, aiffName, "-t", "wav", destName };
			destination.delete();
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TTSException(e.getMessage(), e);
		} finally {
			try {
				if (writer!=null)	writer.close();
				if (fout!=null)	fout.close();
			} catch (IOException e) {}
			(new File(aiffName)).delete();
			(new File(txtName)).delete();
		}
	}

}
