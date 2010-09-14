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
package se_tpb_speechgen2.external.dummy;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.daisy.util.file.FileUtils;

import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.adapters.AbstractTTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * 
 * @author Romain Deltour
 * 
 */
public class DummyTTS extends AbstractTTSAdapter {

	private File dummyWavFile;

	public DummyTTS(TTSUtils ttsUtils, Map<String, String> params) {
		super(ttsUtils, params);
	}
	
	
	@Override
	public void init() {
		super.init();
		try {
			dummyWavFile = new File(getClass().getResource("hello.wav").toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}


	@Override
	public void read(String line, File destination) throws IOException,
			TTSException {
		FileUtils.copy(dummyWavFile, destination);
	}

}
