/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
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

package se_tpb_speechgenerator;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.StreamRedirector;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;

/**
 * Uses simple stdin-stdout piping to communicate with an 
 * external sapi process.
 * 
 * Looks for the system variable <tt>org.daisy.debug</tt> to decide
 * whether to print debug messages or not.
 * @author Martin Blomberg
 */
public final class SAPIImpl extends ExternalTTS {

	private OutputStreamWriter mWriter;			// writes data to the native process
	private BufferedReader mReader;				// reads data from the native process
	private String mAbsoluteNativeLocation;		// location of the native process
	private Process mNativeSapi;				// the native process
	private String mSapiVoiceSelection;			// sapi mark-up for special voice wishes. May remain null.
	
	
	/**
	 * Constructs an instance.
	 * @param params A map containing parameters for the run. Specifically:
	 * the parameter <tt>TTSBuilder.BINARY</tt> must point to the binary program
	 * used for communication with SAPI.
	 * 
	 * @throws IOException
	 */
	public SAPIImpl(Map params) throws IOException {
		super(params);
		String bin = (String) params.get(TTSBuilder.BINARY);
		if (null == bin) {
			String message = "Missing property " + TTSBuilder.BINARY +
			" for tts " + getClass().getName();
			throw new IllegalArgumentException(message);
		} else {
			setBinaryPath(new File(bin));
		}
		initialize();
	}	
	
	/**
	 * Initializes the program. Opens streams and starts external process.
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		Runtime rt = Runtime.getRuntime();
		mNativeSapi = rt.exec(mAbsoluteNativeLocation);
		StreamRedirector sr = new StreamRedirector(mNativeSapi.getErrorStream(), System.err);
		sr.start();
		
		if (mReader != null && mReader.ready()) {
			mReader.close();
		}
		
		if (mWriter != null) {
			mWriter.close();
		}
		
		if (null != parameters.get("sapiVoiceSelection")) {
			mSapiVoiceSelection = (String) parameters.get("sapiVoiceSelection");
		}
		
		mReader = new BufferedReader(new InputStreamReader(mNativeSapi.getInputStream()));
		mWriter = new OutputStreamWriter(
				new BufferedOutputStream(mNativeSapi.getOutputStream()), "utf-8");
	}
	
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgenerator.ExternalTTS#sayImpl(org.w3c.dom.Document, java.io.File)
	 */
	protected long sayImpl(Document doc, File file) throws IOException, UnsupportedAudioFileException, TransformerRunException {		
		String content = "";
		try {
			content = xsltFilter(doc);
		} catch (CatalogExceptionNotRecoverable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XSLTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (content.length() == 0) {
			return 0;
		}
		
		return sayImpl(content, file);
	}

	
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgenerator.ExternalTTS#sayImpl(java.lang.String, java.io.File)
	 */
	protected long sayImpl(String line, File file) throws IOException, UnsupportedAudioFileException, TransformerRunException {
		if (line.length() == 0) {
			return 0;
		}
		
		line = regexFilter(line);
		line = yearFilter(line);
		line = replaceUChars(line);
		line = normalizeWhitespace(line);
		String noSelection = null;
		
		if (mSapiVoiceSelection != null) {
			noSelection = line;
			line = "<voice optional=\"" + mSapiVoiceSelection + "\">" + line + "</voice>";
		}
		
		long timeVal = 0;
		send(file.getAbsolutePath());
		send(line);
		timeVal = getAudioLength(file); 
		
		if (timeVal == 0 && noSelection != null) {
			// an error occured, 
			// try to speak without the sapi voice selection.
			initialize();
			send(file.getAbsolutePath());
			send(noSelection);
			timeVal = getAudioLength(file);
			
			// we were better off without the voice selection, 
			// don't use it next time.
			if (timeVal > 0) {
				System.out.println("SAPIImpl#say(String, File): Error using SAPI's voice selection, continuing with SAPI's default voice.");
				System.out.println("SAPIImpl#say(String, File): Processed line: \"" + line +"\"");
				mSapiVoiceSelection = null;
				parameters.remove("sapiVoiceSelection");
			}
		}
		
		if (timeVal == 0) {
			DEBUG("SAPIImpl#say(String, File): Line = \"" + line + "\"");
			String message = "SAPIImpl#say(String, File): error speaking sentence: " + line + ",\n" +
				"error writing file " + file.getAbsolutePath();
			throw new TransformerRunException("An error occured using SAPI:\n" + message);
		}
		return timeVal;
	}
	
	
	/** 
	 * Reads one line of data from the SAPI-program just
	 * to get the two processes synchronized. That line
	 * must be the text string "OK", everything else is considered
	 * an error.
	 * Calculates and returns the duration of the generated
	 * speech (millis).
	 * 
	 * @param file the audio file (.wav) to examine.
	 * @return the duration of the file in miliseconds.
	 */
	private long getAudioLength(File file) throws IOException, UnsupportedAudioFileException {
		// just to get the two processes synchronized:
		String line = mReader.readLine();
		if (line == null || !line.equals("OK")) {
			return 0;
		}
		
		AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
		AudioFormat format = aff.getFormat();
		return (long)(1000.0 * aff.getFrameLength() / format.getFrameRate());
	}
	
	
	/**
	 * Sends the string str to the sapi executable.
	 * 
	 * @param str the string to read.
	 */
	private void send(String str) throws IOException {
		if (null == mAbsoluteNativeLocation) {
			throw new IllegalArgumentException("Path to TTS binary must be specified explicitly!");
		}
		
		str = str.trim() + System.getProperty("line.separator");
		mWriter.write(str);
		mWriter.flush();
	}
	

	/**
	 * Closes streams, interrupts the sapi process .
	 */
	public void close() throws IOException {

		if (null != mWriter) {
			mWriter.write(System.getProperty("line.separator"));
			mWriter.flush();
			mWriter.close();
			mWriter = null;
			mReader.close();
			mReader = null;
		}
		
		long pollInterval = 100;
		try {
			try {
				Thread.sleep(pollInterval);
			} catch (InterruptedException ignored) {
			}
			mNativeSapi.exitValue();                
		} catch (IllegalThreadStateException e) {
			mNativeSapi.destroy();
		}		
	}


	/**
	 * Sets the path to the external program running sapi.
	 * @param pathToBinary patht to the external program.
	 * @throws IOException
	 */
	public void setBinaryPath(File pathToBinary) throws IOException {
		this.mAbsoluteNativeLocation = pathToBinary.getAbsolutePath();
	}
	
	
	/**
	 * Prints conditional debug messages on System.out. Messages are printed 
	 * if the system property <tt>org.daisy.debug</tt> is set.
	 * 
	 * @param msg the message.
	 */
	private void DEBUG(String msg) {
		if (System.getProperty("org.daisy.debug") != null) {
			System.out.println("DEBUG: " + msg);
		}
	}
}
