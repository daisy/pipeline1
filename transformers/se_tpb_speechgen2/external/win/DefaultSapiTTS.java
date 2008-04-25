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
package se_tpb_speechgen2.external.win;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.daisy.util.wincom.sapi5.ClassFactory;
import org.daisy.util.wincom.sapi5.ISpeechFileStream;
import org.daisy.util.wincom.sapi5.ISpeechVoice;
import org.daisy.util.wincom.sapi5.SpeechStreamFileMode;
import org.daisy.util.wincom.sapi5.SpeechVoiceSpeakFlags;

import se_tpb_speechgen2.audio.AudioFiles;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.adapters.AbstractTTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * @author Romain Deltour
 * 
 */
public class DefaultSapiTTS extends AbstractTTSAdapter {

	ISpeechVoice voice;
	ISpeechFileStream stream = null;

	public DefaultSapiTTS(TTSUtils ttsUtils, Map<String, String> params) {
		super(ttsUtils, params);
		voice = ClassFactory.createSpVoice();
	}

	@Override
	public void read(String line, File destination) throws IOException, TTSException {
		try {
			stream = ClassFactory.createSpFileStream();
			stream.open(destination.getAbsolutePath(),
					SpeechStreamFileMode.SSFMCreateForWrite, false);
			voice.audioOutputStream(stream);
			voice.speak(line, SpeechVoiceSpeakFlags.SVSFIsXML);
			voice.waitUntilDone(-1);
		} catch (Exception e) {
			throw new TTSException(e.getMessage(), e);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	@Override
	public void close() throws IOException, TTSException {
		if (stream != null) {
			stream.dispose();
		}
		if (voice != null) {
			voice.dispose();
		}
	}
	

}
