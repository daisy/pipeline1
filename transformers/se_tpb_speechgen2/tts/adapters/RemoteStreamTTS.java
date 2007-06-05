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
package se_tpb_speechgen2.tts.adapters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

import org.daisy.util.file.StreamRedirector;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;

import se_tpb_speechgen2.tts.TTSConstants;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.concurrent.TTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * Uses stdin-stdout piping to communicate with an 
 * external tts process.
 * 
 * This process writes text on stdout and reads the number 
 * of bytes, new line + audio data on stdin. Typically
 * used when distributing the tts system.
 * 
 * @author Martin Blomberg
 *
 */
public class RemoteStreamTTS implements TTSAdapter {

	private TTSUtils mTu;					// utility functions
	private Map<String, String> mParams;	// configuration parameters defined by user
	
	private String mLastText;				// the text most recently being processed
	private String mCommand;				// the command to use to start external tts process
	private Process mProcess;				// the abstraction of the external tts process
	
	private OutputStreamWriter mWriter;		// writes data to the external tts process
	private InputStream mInput;				// reads data from the external tts process
	private long mTimeout = 30000;			// default timeout in milliseconds
	private StreamRedirector mStreamRedirector;	// a stream redirector, piping the external stderr to this stderr.
	
	/**
	 * Constructs a new instance given the parameters in <code>params</code>
	 * and the util functions <code>tu</code>. 
	 * None of those may be <code>null</code>.
	 * @param tu the utility functions.
	 * @param params the parameters provided by configuration.
	 * @throws IOException
	 */
	public RemoteStreamTTS(TTSUtils tu, Map<String, String> params) throws IOException {
		if (tu != null) {
			mTu = tu;
		} else {
			String msg = "No TTSUtils supplied! Unable to continue";
			throw new IllegalArgumentException(msg);
		}
		
		if (params != null) {
			mParams = params;
			
			if (null != mParams.get(TTSConstants.TIMEOUT)) {
				String s = mParams.get(TTSConstants.TIMEOUT);
				mTimeout = Long.parseLong(s);
			}
			
			mCommand = mParams.get(TTSConstants.COMMAND);
			Runtime rt = Runtime.getRuntime();
			mProcess = rt.exec(mCommand);
			
			mInput = mProcess.getInputStream();
			mWriter = new OutputStreamWriter(
					new BufferedOutputStream(mProcess.getOutputStream()), "utf-8");
			
			mStreamRedirector = new StreamRedirector(mProcess.getErrorStream(), System.err, true);
			mStreamRedirector.start();
			
		} else {
			String msg = "No parameters supplied! Unable to continue";
			throw new IllegalArgumentException(msg);
		}
	}
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSInstance#close()
	 */
	public void close() throws IOException, TTSException {
		
		if (null != mWriter) {
			mWriter.write(System.getProperty("line.separator"));
			mWriter.flush();
			
			mWriter.close();
			mWriter = null;
			mInput.close();
			mInput = null;
		}
		
		int exitVal = -1;
		long pollInterval = 1000;
		long startTime = System.currentTimeMillis();
		
		do {
			try {
				exitVal = mProcess.exitValue();
			} catch (IllegalThreadStateException ignored) {
				
				try {
					Thread.sleep(pollInterval);
				} catch (InterruptedException ignoredAgain) {
					/* nothing here, we are just trying to shutdown. */
				}
			}	
		} while (exitVal < 0 && (mTimeout > (System.currentTimeMillis() - startTime)));
		
		if (exitVal < 0) {
			mProcess.destroy();
		}
		
		if (exitVal > 0) {
			String msg = "Filibuster error occurred: At least " + exitVal + " skipped phrase" + (exitVal > 1? "s" : "") 
			+ ", please refer to the filibuster error logs for more information.";
			throw new TTSException(msg);
		} else if (exitVal < 0) {
			String msg = "Filibuster returned error: " + exitVal + ", " +
					"please refer to the filibuster error logs for more information.";
			throw new TTSException(msg);
		}
	}

	
	/**
	 * Reads the announcements in <code>announcements</code>.
	 * @param announcements a list of <code>StartElement</code>s 
	 * containing announcements.
	 * @param attrName a <code>QName</code> pointing out the attribute
	 * holding announcement text.
	 * @return a <code>byte[]</code>containing the produced audio file.
	 * @throws IOException
	 * @throws TTSException
	 */
	private byte[] read(List<StartElement> announcements, QName attrName) throws IOException, TTSException {
		String line = TTSUtils.concatAttributes(announcements, attrName);		
		line = mTu.str2input(line);
		if (!isSpeakable(line)) {
			return new byte[0];
		}
		
		send(line);
		return receive();
	}

	/**
	 * Reads the text content of <code>doc</code>.
	 * @param doc the xml fragment.
	 * @return a <code>byte[]</code> containing the produced audio file.
	 * @throws IOException
	 * @throws TTSException
	 */
	private byte[] read(Document doc) throws IOException, TTSException {
		String line = null;
		try {		
			line = mTu.dom2input(doc);
		} catch (XSLTException e) {
			throw new TTSException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new TTSException(e.getMessage(), e);
		}
		
		if (!isSpeakable(line)) {
			return new byte[0];
		}
		
		send(line);
		return receive();
	}
	
	/**
	 * Sends one line of text to the (remote) TTS using the piped reader/input stream.
	 * @param line the text to be sent to the TTS.
	 * @throws IOException
	 */
	private void send(String line) throws IOException {		
		mLastText = line;
		mWriter.write(line);
		mWriter.write("\n");
		mWriter.flush();
	}
	
	/**
	 * Reads the audio data on the following form:
	 * 
	 * First, there is ascii characters telling how many audio bytes 
	 * will be delivered. That "size string" is ended with a new line 
	 * character.
	 * 
	 * Next, there comes the audio data, no new lines or anything
	 * strange. The audio data must include a RIFF header (ie. not raw PCM).
	 * 
	 * @return a byte[] containing the contents of the file.
	 * @throws IOException 
	 * @throws IOException
	 */
	private byte[] receive() throws TTSException, IOException {	
		// read the first line, it contains the number of audio bytes
		String size = "";
		int c;
		
		String timeOutMessage = "Wait for the TTS data timed out, was wating for " +
				"audio data for >>" + mLastText + "<<, timeout was " + mTimeout + " ms";
		
		try {
			TTSUtils.awaitIO(mInput, mTimeout);		
		} catch (IOException e) {
			throw new TTSException(timeOutMessage, e);
		}
		
		while ((c = mInput.read()) != '\n') {
			if (-1 == c) {
				String msg = "Connection to the TTS was lost unexpectedly, " +
						"read EOF (InputStream.read() = -1) instead of ascii " +
						"telling the number of audio bytes produced.\n" +
						"Waited for speech >>" + mLastText + "<<";
				throw new TTSException(msg);
			}
			size += (char) c;
			try {
				TTSUtils.awaitIO(mInput, mTimeout);
			} catch (IOException e) {
				throw new TTSException(timeOutMessage, e);
			}
		}
		
		// read the audio
		byte[] audio = new byte[Integer.parseInt(size)];
		int len = audio.length;
		do {
			try {
				TTSUtils.awaitIO(mInput, mTimeout);
			} catch (IOException e) {
				throw new TTSException(timeOutMessage, e);
			}
			len -= mInput.read(audio, audio.length - len, len);	
		} while (len > 0);
		
		return audio;
	}
	


	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSInstance#read(java.util.List, javax.xml.namespace.QName, java.io.File)
	 */
	public long read(List<StartElement> announcements, QName attrName, File destination) throws IOException, TTSException {
		long dur = 0;
		byte[] audio = read(announcements, attrName);
		try {
			dur = TTSUtils.writeAudio(audio, destination);
		} catch (UnsupportedAudioFileException e) {
			throw new TTSException(e.getMessage(), e);
		}
		
		return dur;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSInstance#read(org.w3c.dom.Document, java.io.File)
	 */
	public long read(Document doc, File destination) throws IOException, TTSException {
		long dur = 0;
		byte[] audio = read(doc);
		try {
			dur = TTSUtils.writeAudio(audio, destination);
		} catch (UnsupportedAudioFileException e) {
			throw new TTSException(e.getMessage(), e);
		}
		
		return dur;
	}
	
	/**
	 * Returns true iff the line is speakable.
	 * @param line the text to speech.
	 * @return true if the line is speakable, false otherwise.
	 */
	private static boolean isSpeakable(String line) {
		/*
		 * During a meeting 2007-02-20 Kåre and Christina claimed that
		 * Filibuster will survive any input character without producing
		 * errors, short silence will be returned for unspeakable characters.
		 * 
		 * That means that no specific input check needs to be done here, just
		 * make sure the line isn't null or empty.
		 * 
		 * That amounts to:
		 */	
		
		if (null == line) {
			return false;
		}
		
		if (line.trim().length() == 0) {
			return false;
		}
		
		return true;
	}
	
	private static String formatTime(long time) {
		StringBuffer sb = new StringBuffer();
		
		long ms = time % 1000;
		time /= 1000;
		long s = time % 60;
		time /= 60;
		long m = time % 60;
		time /= 60;
		long h = time;
		
		// hours
		sb.append(h);
		sb.append('h');
		
		// minutes
		if (m < 10) {
			sb.append('0');
		}
		sb.append(m);
		sb.append('m');
		
		// seconds
		if (s < 10) {
			sb.append('0');
		}
		sb.append(s);
		sb.append('.');
		
		// and the ms (as part of the sec part).. 
		if (ms < 10) {
			sb.append('0');
			sb.append('0');
		} else if (ms < 100) {
			sb.append('0');
		}
		sb.append(ms);
		sb.append('s');
		
		return sb.toString();
	}
	
	private static String formatByteSize(long bytes) {
		StringBuffer sb = new StringBuffer();
		
		long mbytes = bytes / (1024 * 1024);
		
		if (mbytes < 1) {
			sb.append(bytes);
			sb.append(" b");
		} else {
			sb.append('~');
			sb.append(mbytes);
			sb.append(" m");
		}
		
		return sb.toString();
	}
}
